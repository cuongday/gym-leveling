#!/usr/bin/env python3
"""Validate Gym Leveling training content (content/exercises.yaml + content/templates/*.yaml).

Usage:  python3 -I content/scripts/validate.py [--content-dir DIR] [--times]
Needs:  Python 3.8+, PyYAML.
Exit:   0 when there are no errors (warnings are allowed), 1 otherwise.
"""
import argparse
import sys
from pathlib import Path

try:
    import yaml
except ImportError:  # pragma: no cover
    sys.exit("PyYAML is required: python3 -m venv /tmp/glvenv && /tmp/glvenv/bin/pip install pyyaml")

LANGS = ("vi", "en")
MUSCLES = {"chest", "back", "shoulders", "biceps", "triceps", "core", "glutes", "quads",
           "hamstrings", "calves", "full_body", "cardio"}
EQUIPMENT = {"none", "dumbbell", "bench", "resistance_band", "barbell", "machine", "cable",
             "pullup_bar", "mat"}
MEASURES = {"reps", "time", "reps_per_side", "distance_m"}
GOALS = {"fat_loss", "muscle_gain", "fitness", "maintain"}
LEVELS = {"beginner", "intermediate", "advanced", "light"}
SPLITS = {"full_body", "upper_lower", "push_pull_legs", "body_part", "circuit"}
MINUTES = {20, 30, 45, 60}
# Equipment each location may use. A mat and a resistance band are assumed cheap enough to be
# available everywhere; a home_dumbbell user is assumed to have a sturdy chair/bench-like surface
# only via "none" exercises, so "bench" is gym-only.
LOCATION_EQUIPMENT = {
    "home_bodyweight": {"none", "mat", "resistance_band"},
    "home_dumbbell": {"none", "mat", "resistance_band", "dumbbell"},
    "gym": EQUIPMENT,
}
SAFE_MAX_DIFFICULTY = 2
RANGES = {"sets": (1, 6), "reps": (1, 30), "duration_sec": (10, 1800), "rest_sec": (15, 240),
          "distance_m": (10, 10000)}
AMOUNT_KEY = {"reps": "reps", "reps_per_side": "reps", "time": "duration_sec", "distance_m": "distance_m"}

# Session time estimate (used for --times and the budget check).
SEC_PER_REP = 3          # controlled tempo
WARMUP_SEC = 5 * 60      # implicit warm-up
TRANSITION_SEC = 30      # moving between exercises / setting up
BUDGET_TOLERANCE = 1.10  # peak week may exceed session_minutes by up to 10% before warning


class Report:
    def __init__(self):
        self.errors, self.warnings = [], []

    def err(self, where, msg):
        self.errors.append(f"{where}: {msg}")

    def warn(self, where, msg):
        self.warnings.append(f"{where}: {msg}")


def is_int(v):
    return isinstance(v, int) and not isinstance(v, bool)


def check_localized(r, where, value, kind="str"):
    if not isinstance(value, dict):
        r.err(where, "must be a {vi, en} mapping")
        return
    extra = set(value) - set(LANGS)
    if extra:
        r.err(where, f"unexpected languages {sorted(extra)}")
    for lang in LANGS:
        v = value.get(lang)
        if kind == "str":
            if not isinstance(v, str) or not v.strip():
                r.err(where, f"'{lang}' missing or empty")
        else:
            if not isinstance(v, list) or not v or not all(isinstance(x, str) and x.strip() for x in v):
                r.err(where, f"'{lang}' must be a non-empty list of non-empty strings")
    if kind == "list" and all(isinstance(value.get(l), list) for l in LANGS):
        if len(value["vi"]) != len(value["en"]):
            r.warn(where, "vi and en have a different number of items")


def check_range(r, where, key, value):
    lo, hi = RANGES[key]
    if not is_int(value) or not lo <= value <= hi:
        r.err(where, f"{key}={value!r} must be an integer in {lo}..{hi}")
        return False
    return True


def load_yaml(r, path):
    try:
        with open(path, encoding="utf-8") as f:
            return yaml.safe_load(f)
    except yaml.YAMLError as e:
        r.err(str(path), f"YAML parse error: {e}")
    except OSError as e:
        r.err(str(path), f"cannot read: {e}")
    return None


def validate_exercises(r, path):
    data = load_yaml(r, path)
    if not isinstance(data, dict):
        r.err(str(path), "top level must be a mapping")
        return {}
    if data.get("version") != 1:
        r.err(str(path), "version must be 1")
    items = data.get("exercises")
    if not isinstance(items, list) or not items:
        r.err(str(path), "'exercises' must be a non-empty list")
        return {}

    by_id = {}
    required = ["id", "name", "description", "cues", "safety", "muscle_groups", "equipment",
                "difficulty", "measure", "easier", "harder", "media", "reviewed"]
    for i, ex in enumerate(items):
        where = f"exercises[{i}]"
        if not isinstance(ex, dict):
            r.err(where, "must be a mapping")
            continue
        eid = ex.get("id")
        if isinstance(eid, str):
            where = f"exercise '{eid}'"
        for key in required:
            if key not in ex:
                r.err(where, f"missing field '{key}'")
        unknown = set(ex) - set(required)
        if unknown:
            r.err(where, f"unknown fields {sorted(unknown)}")
        if not isinstance(eid, str) or not eid or not eid.replace("_", "").isalnum() or eid != eid.lower():
            r.err(where, "id must be a non-empty snake_case string")
            continue
        if eid in by_id:
            r.err(where, "duplicate id")
            continue
        by_id[eid] = ex

        for key in ("name", "description", "safety"):
            if key in ex:
                check_localized(r, f"{where}.{key}", ex[key])
        if "cues" in ex:
            check_localized(r, f"{where}.cues", ex["cues"], kind="list")
            for lang in LANGS:
                cues = (ex["cues"] or {}).get(lang) if isinstance(ex["cues"], dict) else None
                if isinstance(cues, list) and not 2 <= len(cues) <= 4:
                    r.err(f"{where}.cues.{lang}", "must have 2-4 cues")

        mg = ex.get("muscle_groups")
        if not isinstance(mg, list) or not mg:
            r.err(where, "muscle_groups must be a non-empty list")
        else:
            bad = [m for m in mg if m not in MUSCLES]
            if bad:
                r.err(where, f"unknown muscle_groups {bad}")
        eq = ex.get("equipment")
        if not isinstance(eq, list) or not eq:
            r.err(where, "equipment must be a non-empty list")
        else:
            bad = [e for e in eq if e not in EQUIPMENT]
            if bad:
                r.err(where, f"unknown equipment {bad}")
            if "none" in eq and len(eq) > 1:
                r.err(where, "'none' cannot be combined with other equipment")
        d = ex.get("difficulty")
        if not is_int(d) or not 1 <= d <= 5:
            r.err(where, f"difficulty={d!r} must be an integer 1..5")
        if ex.get("measure") not in MEASURES:
            r.err(where, f"measure={ex.get('measure')!r} must be one of {sorted(MEASURES)}")
        media = ex.get("media")
        if not isinstance(media, dict) or not isinstance(media.get("youtube_query"), str) \
                or not media["youtube_query"].strip():
            r.err(where, "media.youtube_query must be a non-empty string")
        elif "http" in media["youtube_query"].lower():
            r.err(where, "media.youtube_query must be a search query, not a URL")
        if not isinstance(ex.get("reviewed"), bool):
            r.err(where, "reviewed must be true or false")

    # Cross references (after all ids are known).
    for eid, ex in by_id.items():
        where = f"exercise '{eid}'"
        for key in ("easier", "harder"):
            ref = ex.get(key)
            if ref is None:
                continue
            if ref == eid:
                r.err(where, f"{key} references itself")
            elif ref not in by_id:
                r.err(where, f"{key} references unknown exercise '{ref}'")
            else:
                d, rd = ex.get("difficulty"), by_id[ref].get("difficulty")
                if is_int(d) and is_int(rd):
                    if key == "easier" and rd > d:
                        r.warn(where, f"easier '{ref}' has higher difficulty ({rd} > {d})")
                    if key == "harder" and rd < d:
                        r.warn(where, f"harder '{ref}' has lower difficulty ({rd} < {d})")
        if ex.get("easier") and ex.get("easier") == ex.get("harder"):
            r.err(where, "easier and harder point to the same exercise")
    return by_id


def estimate_seconds(line, ex, sets_delta=0, reps_delta=0, dur_delta=0):
    sets = max(1, line["sets"] + sets_delta)
    measure = ex["measure"]
    if measure == "time":
        work = line["duration_sec"] + dur_delta
    elif measure == "distance_m":
        work = line["distance_m"] * 0.4  # ~brisk walk/jog pace; rough
    else:
        reps = max(1, line["reps"] + reps_delta)
        work = reps * SEC_PER_REP * (2 if measure == "reps_per_side" else 1)
    return sets * (work + line["rest_sec"]) + TRANSITION_SEC


def validate_template(r, path, exercises, seen_ids, show_times):
    t = load_yaml(r, path)
    where = path.name
    if not isinstance(t, dict):
        r.err(where, "top level must be a mapping")
        return None
    required = ["version", "id", "name", "summary", "goals", "level", "location", "sessions_per_week",
                "session_minutes", "split", "weeks", "sessions", "weekly_progression", "reviewed"]
    for key in required:
        if key not in t:
            r.err(where, f"missing field '{key}'")
    unknown = set(t) - set(required)
    if unknown:
        r.err(where, f"unknown fields {sorted(unknown)}")
    if t.get("version") != 1:
        r.err(where, "version must be 1")
    tid = t.get("id")
    if not isinstance(tid, str) or not tid:
        r.err(where, "id must be a non-empty string")
    else:
        if path.stem != tid:
            r.err(where, f"file name must be '{tid}.yaml'")
        if tid in seen_ids:
            r.err(where, f"duplicate template id '{tid}'")
        seen_ids.add(tid)
    for key in ("name", "summary"):
        if key in t:
            check_localized(r, f"{where}.{key}", t[key])
    goals = t.get("goals")
    if not isinstance(goals, list) or not goals or any(g not in GOALS for g in goals):
        r.err(where, f"goals must be a non-empty list from {sorted(GOALS)}")
    elif len(set(goals)) != len(goals):
        r.err(where, "duplicate goals")
    level, location = t.get("level"), t.get("location")
    if level not in LEVELS:
        r.err(where, f"level={level!r} must be one of {sorted(LEVELS)}")
    if location not in LOCATION_EQUIPMENT:
        r.err(where, f"location={location!r} must be one of {sorted(LOCATION_EQUIPMENT)}")
    spw = t.get("sessions_per_week")
    if not is_int(spw) or not 2 <= spw <= 6:
        r.err(where, f"sessions_per_week={spw!r} must be an integer 2..6")
    minutes = t.get("session_minutes")
    if minutes not in MINUTES:
        r.err(where, f"session_minutes={minutes!r} must be one of {sorted(MINUTES)}")
    if t.get("split") not in SPLITS:
        r.err(where, f"split={t.get('split')!r} must be one of {sorted(SPLITS)}")
    if t.get("weeks") != 4:
        r.err(where, "weeks must be 4")
    if level == "light":
        if is_int(spw) and spw > 3:
            r.err(where, "light templates must have at most 3 sessions per week")
        if is_int(minutes) and minutes > 30:
            r.err(where, "light templates must be at most 30 minutes")
    if not isinstance(t.get("reviewed"), bool):
        r.err(where, "reviewed must be true or false")

    prog = t.get("weekly_progression")
    deltas = {}
    if not isinstance(prog, list) or len(prog) != 4:
        r.err(where, "weekly_progression must have exactly 4 entries")
    else:
        for i, p in enumerate(prog):
            pw = f"{where}.weekly_progression[{i}]"
            if not isinstance(p, dict) or p.get("week") != i + 1:
                r.err(pw, f"must be a mapping with week: {i + 1}")
                continue
            vals = []
            for key in ("sets_delta", "reps_delta", "duration_delta_sec"):
                v = p.get(key)
                if not is_int(v):
                    r.err(pw, f"{key} must be an integer")
                vals.append(v if is_int(v) else 0)
            if set(p) - {"week", "sets_delta", "reps_delta", "duration_delta_sec"}:
                r.err(pw, "unknown fields")
            if not -2 <= vals[0] <= 2 or not -5 <= vals[1] <= 5 or not -30 <= vals[2] <= 30:
                r.err(pw, "deltas out of sane range (sets ±2, reps ±5, duration ±30s)")
            deltas[i + 1] = vals
        if level == "light" and any(v[0] > 0 for v in deltas.values()):
            r.err(where, "light templates must not add sets in progression")

    allowed_eq = LOCATION_EQUIPMENT.get(location, set())
    sessions = t.get("sessions")
    if not isinstance(sessions, list) or not sessions:
        r.err(where, "sessions must be a non-empty list")
        return t
    if is_int(spw) and len(sessions) != spw:
        r.err(where, f"has {len(sessions)} sessions but sessions_per_week={spw}")
    codes = set()
    for si, s in enumerate(sessions):
        sw = f"{where}.sessions[{si}]"
        if not isinstance(s, dict):
            r.err(sw, "must be a mapping")
            continue
        code = s.get("code")
        if not isinstance(code, str) or not code:
            r.err(sw, "code must be a non-empty string")
        elif code in codes:
            r.err(sw, f"duplicate session code '{code}'")
        else:
            codes.add(code)
            sw = f"{where} session {code}"
        if set(s) - {"code", "title", "exercises"}:
            r.err(sw, "unknown fields")
        check_localized(r, f"{sw}.title", s.get("title"))
        lines = s.get("exercises")
        if not isinstance(lines, list) or not lines:
            r.err(sw, "exercises must be a non-empty list")
            continue
        if not 3 <= len(lines) <= 8:
            r.warn(sw, f"{len(lines)} exercises (expected 3-8)")
        valid_lines, used = [], set()
        for li, line in enumerate(lines):
            lw = f"{sw}.exercises[{li}]"
            if not isinstance(line, dict):
                r.err(lw, "must be a mapping")
                continue
            ref = line.get("exercise")
            ex = exercises.get(ref)
            if ex is None:
                r.err(lw, f"unknown exercise '{ref}'")
                continue
            lw = f"{sw} '{ref}'"
            if ref in used:
                r.warn(lw, "exercise appears twice in the same session")
            used.add(ref)
            amount_key = AMOUNT_KEY.get(ex.get("measure"))
            allowed_keys = {"exercise", "sets", "rest_sec", amount_key}
            extra = set(line) - allowed_keys
            if extra:
                r.err(lw, f"unexpected keys {sorted(extra)} for measure '{ex.get('measure')}' "
                          f"(use '{amount_key}')")
            ok = True
            for key in ("sets", amount_key, "rest_sec"):
                if key not in line:
                    r.err(lw, f"missing '{key}'")
                    ok = False
                else:
                    ok = check_range(r, lw, key, line[key]) and ok
            eq = set(ex.get("equipment") or [])
            if not eq <= allowed_eq:
                r.err(lw, f"needs {sorted(eq - allowed_eq)} which location '{location}' does not have")
            d = ex.get("difficulty")
            if level == "light" and is_int(d) and d > SAFE_MAX_DIFFICULTY:
                r.err(lw, f"difficulty {d} not allowed in a light template (max {SAFE_MAX_DIFFICULTY})")
            if level == "beginner" and is_int(d) and d > 3:
                r.err(lw, f"difficulty {d} too high for a beginner template (max 3)")
            if ok:
                valid_lines.append((line, ex))
            # Week-adjusted values must stay inside the sane ranges too.
            if ok and deltas:
                for wk, (sd, rd, dd) in deltas.items():
                    if not 1 <= line["sets"] + sd <= RANGES["sets"][1]:
                        r.err(lw, f"week {wk}: sets {line['sets'] + sd} out of range")
                    if amount_key == "reps" and not 1 <= line["reps"] + rd <= RANGES["reps"][1]:
                        r.err(lw, f"week {wk}: reps {line['reps'] + rd} out of range")
                    if amount_key == "duration_sec" and not \
                            RANGES["duration_sec"][0] <= line["duration_sec"] + dd <= RANGES["duration_sec"][1]:
                        r.err(lw, f"week {wk}: duration {line['duration_sec'] + dd} out of range")

        if is_int(minutes) and valid_lines and len(valid_lines) == len(lines):
            budget = minutes * 60
            wk1 = WARMUP_SEC + sum(estimate_seconds(l, e) for l, e in valid_lines)
            peak = max([wk1] + [WARMUP_SEC + sum(estimate_seconds(l, e, *dv) for l, e in valid_lines)
                                for dv in deltas.values()])
            if wk1 > budget:
                r.err(sw, f"week-1 estimate {wk1 / 60:.1f} min exceeds session_minutes={minutes}")
            elif peak > budget * BUDGET_TOLERANCE:
                r.warn(sw, f"peak-week estimate {peak / 60:.1f} min exceeds {minutes} min by >10%")
            if wk1 < budget * 0.5:
                r.warn(sw, f"week-1 estimate {wk1 / 60:.1f} min is under half of {minutes} min")
            if show_times:
                print(f"  {where} {code}: week1 ~{wk1 / 60:.0f} min, peak ~{peak / 60:.0f} min / {minutes}")
    return t


def main():
    ap = argparse.ArgumentParser(description=__doc__.splitlines()[0])
    ap.add_argument("--content-dir", default=str(Path(__file__).resolve().parent.parent))
    ap.add_argument("--times", action="store_true", help="print per-session time estimates")
    args = ap.parse_args()
    root = Path(args.content_dir)
    r = Report()

    exercises = validate_exercises(r, root / "exercises.yaml")
    tpl_dir = root / "templates"
    paths = sorted(tpl_dir.glob("*.yaml")) if tpl_dir.is_dir() else []
    if not paths:
        r.err(str(tpl_dir), "no templates found")
    templates, seen = [], set()
    for p in paths:
        t = validate_template(r, p, exercises, seen, args.times)
        if isinstance(t, dict):
            templates.append(t)

    # Coverage: every goal appears in at least one non-light template per location; one light per location.
    for loc in LOCATION_EQUIPMENT:
        here = [t for t in templates if t.get("location") == loc]
        goals = {g for t in here if t.get("level") != "light" for g in (t.get("goals") or [])}
        missing = GOALS - goals
        if missing:
            r.err(f"coverage {loc}", f"no template for goals {sorted(missing)}")
        if not any(t.get("level") == "light" for t in here):
            r.err(f"coverage {loc}", "no light (safety_flag) template")
        for lvl in ("beginner", "intermediate", "advanced"):
            n = sum(1 for t in here if t.get("level") == lvl)
            if n < 2:
                r.warn(f"coverage {loc}", f"only {n} '{lvl}' template(s); the app offers 2 options")

    used = {l.get("exercise") for t in templates for s in (t.get("sessions") or []) if isinstance(s, dict)
            for l in (s.get("exercises") or []) if isinstance(l, dict)}
    unused = sorted(set(exercises) - used)
    if unused:
        r.warn("exercises", f"not used by any template (regression-only?): {', '.join(unused)}")

    reviewed_ex = sum(1 for e in exercises.values() if e.get("reviewed") is True)
    reviewed_t = sum(1 for t in templates if t.get("reviewed") is True)
    by_level = {}
    for t in templates:
        by_level[t.get("level")] = by_level.get(t.get("level"), 0) + 1

    for w in r.warnings:
        print(f"WARN  {w}")
    for e in r.errors:
        print(f"ERROR {e}")
    print("-" * 60)
    print(f"exercises: {len(exercises)} ({reviewed_ex} reviewed)")
    print(f"templates: {len(templates)} ({reviewed_t} reviewed) by level: "
          + ", ".join(f"{k}={v}" for k, v in sorted(by_level.items(), key=lambda kv: str(kv[0]))))
    print(f"result: {len(r.errors)} error(s), {len(r.warnings)} warning(s)")
    return 1 if r.errors else 0


if __name__ == "__main__":
    sys.exit(main())
