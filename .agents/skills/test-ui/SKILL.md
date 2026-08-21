---
name: test-ui
description: Use when testing CHOO console commands or after changing Java code that can affect user-visible command-line behavior.
---

# Test UI

Run the repository's recorded console sessions as exact regression tests.
The test plan is the source of truth for expected user-visible behavior.

## Workflow

1. Read `test/ui-test-plan.md` and add or revise cases when the requested
   command behavior is not covered. Each case requires an aim, complete
   console input, and complete expected output.
2. Confirm Java 25 is active with `java -version` and `javac -version`.
3. From the repository root, run:

   ```bash
   python3 .agents/skills/test-ui/scripts/run_ui_tests.py test/ui-test-plan.md
   ```

4. Show the emitted input/output transcript. On failure, stop the test
   session and report the actual and expected outputs before changing code.

The runner compiles into a temporary directory, so it must not create or
commit `.class` files in the repository.

## Test plan format

Each case has this structure: a `## Test case: NAME` heading, an `Aim:`
line, an `Input:` text-fenced block, and an `Expected output:` text-fenced
block. See `test/ui-test-plan.md` for a complete example.

## Quick reference

| Situation | Action |
| --- | --- |
| Behavior changed intentionally | Update the affected plan case first |
| Case fails | Stop and report actual versus expected |
| All cases pass | Show every recorded transcript |

## Common mistakes

- Comparing only selected lines can miss ordering or separator regressions;
  keep complete expected output.
- Reusing one Java process across cases leaks task state; each case is a new
  process.
- Normalizing whitespace hides UI defects; comparison is exact.
