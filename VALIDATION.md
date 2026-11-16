# Validation — 16 November 2026

## Build and automated checks

- Debug APK assembled with Gradle 8.10.2, JDK 21 and Android SDK 35. Minimum Android version: 10 / API 29.
- Android lint: passed (warnings remain, including hardcoded strings and deprecated Android APIs).
- JVM: 3 keyword tests passed, none skipped or failed.
- DOM: 36 tests passed, none skipped or failed. Covers contact data, existing/user-edited fields, optional demographics, explicit work authorization, radio/select controls, repeatable work and education, Workday-style indexed entries, explicit Add experience expansion, exact answer reuse across employer origins, cover text, step remounts, framework events, no automatic submission, and confirmation signatures for four hiring platforms.
- Android 10 x86_64 software emulator: five core instrumentation tests passed in a completed run: selected-file provider access, encrypted session round-trip, DOCX extraction, confirmed-job deduplication, and scanned-PDF OCR. OCR recognized text rendered into a generated PDF using the bundled offline model.
- Android share/WebView test passed separately: ACTION_SEND text containing an HTTPS URL opens that URL directly in the built-in WebView; the actual WebView engine fills an email field on an HTTPS-origin HTML fixture.

An initial WebView test failed because a callback from the original shared navigation raced with fixture loading. The corrected test stops the original load and waits for the fixture URL; its isolated rerun passed. Combined runs exceeded the software emulator time budget during OCR; the completed isolated core and share runs are the reported results, not the timed-out runs.

## Feature evidence and limits

| Feature | Verified | Remaining validation |
| --- | --- | --- |
| Profile autofill | Native WebView email; DOM contact/location/link matching, repeated work/education, authorization and demographic safeguards | Live employer custom controls and profile-editor usability on physical devices |
| Documents | Android DOCX extraction, scanned PDF OCR, private provider byte access; DOM cover text | Employer upload acceptance, file type/size restrictions, end-to-end file chooser uploads, multi-page scanned résumé accuracy |
| Saved answers | Same exact normalized question reused on two distinct employer origins; similar C and C++ questions remain distinct | Live employer question markup variations |
| Continuous filling | Step DOM remounts preserve existing edits and fill later fields; explicit section expansion; no submit calls or clicks | Real employer navigation and custom date/dropdown widgets |
| Tracker | All four host signatures and submission-intent gates; Android persistence and deduplication | Real employer confirmation pages; these signatures are heuristics and may need maintenance |
| Keyword matching | Three JVM tests for overlap, missing terms, stop words and technical tokens | Real-world résumé/job-description quality; this is literal overlap, not a hiring prediction |

## Platforms

| Platform | Implemented handling | Live result |
| --- | --- | --- |
| Workday | Indexed experience/education containers, HTML/ARIA matching, confirmation signatures | Unverified; custom widgets and employer login flows may need manual entry |
| Greenhouse | General HTML field matching and confirmation signatures | Unverified; custom selects and uploads may need manual entry |
| Ashby | General HTML field matching and confirmation signatures | Unverified; custom controls may need manual entry |
| Lever | General HTML field matching and confirmation signatures | Unverified; actual uploads and confirmation flow not exercised |

Chrome was launched on the emulator and first-run screens were exercised without a Google account. Repeated System UI ANRs, null UI-automation roots and unstable foreground activity prevented a reliable Chrome-menu → Android chooser → Simple walkthrough. **The Android share intent is verified; the complete Chrome UI share sequence is not.**

Live Lever and Ashby HTTP checks returned proxy CONNECT 403. Current Cloud networking allows package-manager domains, not hiring portals. A draft adds `*.myworkdayjobs.com`, `*.greenhouse.io`, `*.ashbyhq.com`, and `*.lever.co`; saving the draft did not apply or publish it. Review/save in environment settings and publish the environment when ready to activate the policy. Other employer-specific login/CDN destinations may also be needed. No branch or environment was published by the agent.

## Supabase

Auth, explicit profile/tracker backup/restore, private document upload/download and owner-only SQL/storage policies are implemented. No Supabase project URL, public key or configured project was supplied. The migration has **not** been deployed, and live signup, login, backup, restore and cross-user isolation are **unverified**. Apply the migration in your project, then configure its HTTPS URL and public publishable/anon key in the app. Never use a service-role key in the app.

## Retrieval and reproducibility

Run `bash scripts/build.sh` in Cloud, then `bash scripts/export.sh` after committing source. The export performs no network operations. `dist/Simple.bundle` preserves every local commit; `git clone Simple.bundle Simple` retrieves the project without pushing. `dist/Simple-source.zip` contains committed source only. `dist/Simple-debug.apk` is a debug build for device testing, and `dist/Simple-tests.apk` contains instrumentation tests. SHA256SUMS accompanies the exported files.

No live employer application was submitted. No push, published branch or PR was created.
