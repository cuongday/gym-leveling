# Training content

Seed content for Gym Leveling: the exercise library and the 4-week plan templates (spec §5.4–5.6).
The backend loads these files; nothing here is shown to users until a certified personal trainer (PT)
has reviewed it and flipped `reviewed` to `true`.

```
content/
  exercises.yaml          # exercise library (70 entries)
  templates/<id>.yaml     # one plan template per file (21 files)
  REVIEW.md               # review sheet for the PT (Vietnamese)
  scripts/validate.py     # validator – run before every commit
```

**Status: DRAFT.** Every exercise and template has `reviewed: false`. No item has been approved by a PT.

## Validate

```bash
python3 -I content/scripts/validate.py           # summary, exit 1 on any error
python3 -I content/scripts/validate.py --times   # also print per-session time estimates
```

Needs Python 3.8+ and PyYAML. If PyYAML is missing, use a throwaway venv outside the repo:
`python3 -m venv /tmp/glvenv && /tmp/glvenv/bin/pip install pyyaml && /tmp/glvenv/bin/python -I content/scripts/validate.py`.

## File format

All localized fields are `{ vi: ..., en: ... }`, and both languages are required and non-empty.

### `exercises.yaml`

```yaml
version: 1
exercises:
  - id: squat_bodyweight            # snake_case, stable, unique; never rename once shipped
    name: { vi: "Squat", en: "Bodyweight squat" }
    description: { vi: "...", en: "..." }   # 2–4 short sentences: setup, movement, breathing
    cues: { vi: ["...","..."], en: ["...","..."] }   # 2–4 form cues
    safety: { vi: "...", en: "..." }        # one line: who should be careful / common mistake
    muscle_groups: [quads, glutes]
    equipment: [none]
    difficulty: 1                           # 1–5
    measure: reps                           # reps | time | reps_per_side | distance_m
    easier: sit_to_stand                    # exercise id or null
    harder: split_squat_bodyweight          # exercise id or null
    media: { youtube_query: "bodyweight squat proper form" }
    reviewed: false
```

| Field | Allowed values |
|---|---|
| `muscle_groups` | chest, back, shoulders, biceps, triceps, core, glutes, quads, hamstrings, calves, full_body, cardio |
| `equipment` | none, dumbbell, bench, resistance_band, barbell, machine, cable, pullup_bar, mat (`none` cannot be combined with anything else) |
| `difficulty` | 1–5. Drives RP per completed exercise: 1–2 → 5 RP, 3 → 7 RP, 4–5 → 10 RP |
| `measure` | `reps`, `reps_per_side` (count is per side), `time` (seconds), `distance_m` |
| `easier` / `harder` | id of another exercise or `null`; never itself. Used when the user rates an exercise "Khó" twice in a row (swap to `easier`) or outgrows it (`harder`). |
| `media.youtube_query` | a search query, never a URL; the content team resolves it to a video/GIF later |

`equipment: [none]` means no training equipment. Everyday furniture (a wall, a sturdy chair, a stair,
a door frame, a heavy table) may still be used and is named in the description.

### `templates/<id>.yaml`

The file name must be `<id>.yaml`.

```yaml
version: 1
id: home_dumbbell_upper_lower_4x_intermediate
name: { vi: "...", en: "..." }
summary: { vi: "...", en: "..." }          # one line shown on the plan card
goals: [muscle_gain, fat_loss]             # one or more of: fat_loss, muscle_gain, fitness, maintain
level: intermediate                        # beginner | intermediate | advanced | light (light = safety_flag only)
location: home_dumbbell                    # home_bodyweight | home_dumbbell | gym
sessions_per_week: 4                       # 2–6
session_minutes: 45                        # 20 | 30 | 45 | 60
split: upper_lower                         # full_body | upper_lower | push_pull_legs | body_part | circuit
weeks: 4
sessions:                                  # exactly sessions_per_week entries, in weekly order
  - code: A
    title: { vi: "Thân trên", en: "Upper body" }
    exercises:
      - { exercise: floor_press_dumbbell, sets: 3, reps: 10, rest_sec: 90 }
      - { exercise: plank, sets: 3, duration_sec: 30, rest_sec: 45 }
weekly_progression:                        # added to the week-1 values; exactly 4 entries
  - { week: 1, sets_delta: 0, reps_delta: 0, duration_delta_sec: 0 }
  - { week: 2, sets_delta: 0, reps_delta: 1, duration_delta_sec: 5 }
  - { week: 3, sets_delta: 1, reps_delta: 0, duration_delta_sec: 5 }
  - { week: 4, sets_delta: -1, reps_delta: -1, duration_delta_sec: 0 }   # deload
reviewed: false
```

Exercise lines:

- Use `reps` for `reps`/`reps_per_side` exercises, `duration_sec` for `time`, `distance_m` for `distance_m`. No other amount key is allowed.
- Ranges: `sets` 1–6, `reps` 1–30, `duration_sec` 10–1800, `rest_sec` 15–240. The values after applying each week's deltas must also stay in range (so a 1-set line can't have a −1 set deload; use 2 shorter sets instead).
- Load (kg) is not in the template. Users pick a weight that leaves ~2–3 reps in reserve; the app's Dễ/Vừa/Khó feedback adjusts reps/time/load on top of `weekly_progression` (spec §5.4).
- A session is one daily quest (spec §5.5). The plan advances by completed session, not by calendar day.

Location → equipment the template may use (enforced by the validator):

| `location` | equipment allowed |
|---|---|
| `home_bodyweight` | none, mat, resistance_band |
| `home_dumbbell` | the above + dumbbell |
| `gym` | everything |

`bench` is gym-only: home dumbbell templates use a chair/floor (`floor_press_dumbbell`, `one_arm_row_dumbbell` on a chair).

### Session time estimate

`5 min warm-up + Σ sets × (work + rest) + 30 s transition per exercise`, where work is
`reps × 3 s` (doubled for `reps_per_side`) or `duration_sec`. Week 1 must fit `session_minutes`
(error); the longest week may exceed it by up to 10% (warning above that).

## Authoring rules (safety principles)

1. **Conservative for beginners.** Beginner templates: 2 sets, 6–12 reps, difficulty ≤ 3 (currently all ≤ 2), supported or machine-based movements, no jumping, progression by reps/time only.
2. **Light templates (`level: light`)** are only for users with `safety_flag` from the health screening (who are also told to consult a doctor). Rules: difficulty ≤ 2 only, ≤ 3 sessions/week, ≤ 30 minutes, no impact, no head-below-heart positions, no added sets in progression. One per location.
3. **No maximal work.** No 1RM tests, no Olympic lifts, no training to failure. Barbell lifts carry a safety line (rack/safety bars or spotter, stop ~2 reps before form breaks) and appear only from intermediate (squat) / advanced (others).
4. **High-impact plyometrics** (e.g. `burpee`) only in advanced templates; beginners and light use the step/no-jump versions.
5. **Balance.** Pair pushing with pulling across the week (weekly pull sets within ~20% of push sets; currently all templates meet this), train legs and hinge patterns, and include core work in most sessions.
6. **Regression paths.** Every exercise that is hard for a beginner has an `easier` link down to a level-1 movement, e.g. `wall_push_up → knee_push_up → incline_push_up → push_up → decline_push_up`.
7. **Plain language.** Vietnamese is written as a Vietnamese PT would say it; English gym names are kept where Vietnamese gym-goers use them (Squat, Plank, Glute bridge, RDL…).
8. **No claims of approval.** Never set `reviewed: true` without a PT's sign-off.

## How to add an exercise

1. Pick a stable snake_case `id` (it is stored in users' history; never rename it).
2. Fill every field in both languages. Description: 2–4 sentences covering setup, movement, breathing. Cues: 2–4. Safety: one line.
3. Set `difficulty` honestly — it decides RP. Link `easier`/`harder` to existing ids (difficulty should go down/up along the chain).
4. Set `reviewed: false`.
5. Run the validator. Add the exercise to `REVIEW.md` so the PT sees it.

## How to add a template

1. Name it `<location>_<split>_<N>x_<level>` (light: `light_<location>_<N>x`) and save it as `templates/<id>.yaml`.
2. Write exactly `sessions_per_week` sessions in weekly order with codes A, B, C…
3. Only use exercises whose equipment fits the location; light templates only difficulty ≤ 2.
4. Keep week 1 within `session_minutes` (check with `--times`).
5. Tag `goals` honestly; each location must keep at least one template per goal, and one `light` template.
6. Set `reviewed: false`, run the validator, and add the template to `REVIEW.md`.

## How `reviewed` works

- `reviewed: false` (default for every new or edited item) – the item is a draft. Non-production loaders may use it; environments shown to real or trial users must skip it (spec §5.4).
- `reviewed: true` – set **only** after a PT has checked that exact version (ideally note the reviewer and date in the commit message).
- Any substantive edit to a reviewed item (sets/reps, exercise swap, description, safety text, difficulty) sets it back to `false` until re-reviewed.
- A template that references an unreviewed exercise should be treated as unreviewed by the loader.
