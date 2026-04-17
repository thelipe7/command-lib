# 🤝 Contributing and Support

## 🐛 Reporting Bugs

Use the bug report template for:

- runtime failures
- broken command parsing
- incorrect usage generation
- Paper or Bukkit incompatibilities
- tab completion regressions

A good report should include:

- server software and version
- Java version
- library version or commit hash
- a minimal command example
- expected behavior
- actual behavior
- stack traces if available

## 💡 Requesting Features

Use the feature request template when proposing:

- new annotations
- new built-in argument resolvers
- new tab completion features
- compatibility improvements
- DX or API improvements

Strong feature requests explain:

- the current limitation
- the proposed API or behavior
- the practical plugin use case
- why the change belongs in the library instead of plugin-level code

## ❓ Asking Questions

Use the question template for:

- integration questions
- command design questions
- resolver usage
- tab completion behavior
- migration or setup doubts

## 🔀 Pull Requests

Keep pull requests focused and easy to review.

Recommended pull request checklist:

- update docs when behavior changes
- keep API changes explicit
- describe compatibility impact
- include reproduction steps for bug fixes
- keep examples minimal and real

## 📚 Documentation Strategy

This repository uses:

- `README.md` as the public landing page
- `documentation/` as the GitHub Wiki content source
- issue templates for support intake quality

If you add public behavior, update the relevant documentation page together with the code.

## 🧠 Practical Guidance

If you are evaluating whether something belongs in the library, prefer additions that improve:

- command declaration clarity
- reusable parsing behavior
- reusable tab completion behavior
- framework ergonomics across multiple plugins
