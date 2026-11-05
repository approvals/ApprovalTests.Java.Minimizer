# ApprovalTests.Java.Minimizer Agent Instructions

Builds a slimmed-down ApprovalTests jar containing only the code a project uses. See [README.md](README.md) for usage.

## Build & Test
- **Full build:** `./build_and_test.sh` (runs `mvn verify`; about 30 seconds).
- **Single test:** `mvn test -Dtest=ClassName#methodName`
- Java 17 (see `.java-version`). Tools are managed with mise (`.mise.toml`).
- `TreeShakerTest` clones the ApprovalTests `31.0.0` tag into the temp directory. It uses a sibling `../ApprovalTests.Java` clone if there is one (offline), and GitHub otherwise.

## Architecture
See [internal_documentation/architecture.md](internal_documentation/architecture.md) for the diagrams and [internal_documentation/PartialEvaluation.md](internal_documentation/PartialEvaluation.md) for the spec and implementation notes. The processes in [internal_documentation/processes/](internal_documentation/processes/) apply here too. `docs/` is published with GitHub Pages and only holds the sample report. In short:
- `CreateSelectionJson.sh` → `PartialEvaluationCli detect` → `SelectionDetector` writes `approvaltests-selection.json`.
- `CreatePartailApprovals.sh` → `PartialEvaluationCli build` → `SourceCheckout` (clone into `.local/`) → `JarBuilder` (baseline build) → `TreeShaker` (mark + sweep the source) → `JarBuilder` (partial jar) → `HtmlReport`.
- `explain` prints why a class was kept (`ShakeReport.keptBecause`).

## Trying It End To End
With [ApprovalTests.java.StarterProject](https://github.com/approvals/ApprovalTests.java.StarterProject) cloned next to this repo:
```bash
cd ../ApprovalTests.java.StarterProject
../ApprovalTests.Java.Minimizer/CreateSelectionJson.sh
../ApprovalTests.Java.Minimizer/CreatePartailApprovals.sh
mvn test -Dmaven.test.dependency.excludes=com.approvaltests:approvaltests,com.approvaltests:approvaltests-util \
         -Dmaven.test.additionalClasspath=$PWD/target/approvaltests-custom/approvaltests-31.0.0-custom.jar
```
The shaken ApprovalTests source is in `.local/approvaltests-<version>/`. Use `git -C .local/approvaltests-<version> diff` to see what was removed.

## Key Rules
- **NEVER** approve a test (create or update `.approved.*` files) automatically. Always ask the user. Commit `.approved.*` files; never commit `.received.*` files.
- Make file changes directly without asking permission (the repo is under git, the user can revert).
- Don't commit `.local/` or `target/` (both gitignored).

## Commit Messages
Use `internal_documentation/scripts/create-commit-message.sh` to build every commit message. Run `bash internal_documentation/scripts/create-commit-message.sh <risk> <intention> "<message>"` and use its stdout as the commit message.
Run it with `--help` to see the risk and intention codes and what they mean.

## Code Style
- Allman brace style (opening brace on its own line). camelCase methods, PascalCase classes.
- Follow the patterns in neighboring files. Don't add unnecessary comments.
- Uses `approvaltests-util` (`FileUtils`, etc.) and JavaParser with its symbol solver.
