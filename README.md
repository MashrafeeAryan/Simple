# Simple 🌱

Native Android 10+ application helper. Share an HTTPS job link as text from Chrome → Simple, or paste a link. Browse the listing and tap Apply inside the WebView, then tap **Fill application**. Simple never submits an application. Review every value before submitting yourself.

## Features

- Local contact details, links, multiple work and education entries, and explicitly saved authorization/demographic answers.
- PDF/DOCX résumé and cover letter import; PDF text extraction falls back to bundled on-device OCR for scanned pages. Up to 20 MB and 50 OCR pages. Pasted résumé and cover text are supported.
- Exact normalized question reuse. No generated answers.
- Continuous filling across loaded steps, with pause control. Explicitly labeled Add work experience / Add education buttons can expand rows. Existing answers remain unchanged.
- Local tracker records recognized Workday, Greenhouse, Ashby and Lever confirmation text after recent user submission intent. This is a heuristic, not a receipt from the employer.
- Literal matched/missing résumé keywords, with job description pasted or captured from the current page.
- Supabase email accounts and explicit cloud backup/restore of profile, answers, tracker and private documents. Device use works before account setup. No Simplify account or proprietary engine.

## Use

1. Open Profile and save your details and repeatable entries. Optional fields stay blank until explicitly saved.
2. Open Documents, select a résumé and optional cover letter. Review extracted text, especially OCR output.
3. Share or paste a job link. Login directly to the employer portal if required.
4. Tap Fill application. When an upload field is tapped, choose the selected résumé, cover letter, or another file in the system picker. Attachments require that user action; the engine cannot assign a local filesystem path to a webpage.
5. Advance through the application, review and submit yourself. Pause autofill whenever desired.

## Build and test

Requires JDK 17+ (Cloud uses 21), Android SDK platform 35, build tools 34+, and Gradle 8.10.2. Set ANDROID_HOME or local.properties. Run `bash scripts/build.sh` in Cloud. Unit tests are under app/src/test, Android instrumentation under app/src/androidTest, and DOM regressions under tests (`npm ci && npm test`). No secret values belong in the checkout.

Configure Supabase using [supabase/README.md](supabase/README.md). A running project is required for live account/sync validation; migrations are supplied but are not automatically deployed.

## Platform boundaries

The engine matches native HTML inputs, selects and textareas using explicit field aliases, associated labels and ARIA labels. Workday indexed history containers have dedicated handling. All four platform hostnames have confirmation signatures. This does **not** establish end-to-end compatibility with every employer portal. Custom JavaScript dropdowns, shadow DOM, cross-origin iframes, CAPTCHA, unsupported section structures, date widgets and portal-specific upload constraints may need manual entry. Do not infer live-platform support from synthetic fixtures. See [VALIDATION.md](VALIDATION.md) for observed results.

## Privacy and data

Private Android app storage holds the profile and documents. Android backup is disabled. Supabase tokens are encrypted using an Android Keystore key. Cloud data uses owner-only database RLS and private storage policies; it is not end-to-end encrypted. Sign out clears the device session but retains the local profile. Uninstalling removes local data. Do not use a shared phone profile for multiple people. Cloud restore replaces the local profile; upload is explicit, not automatic conflict-resolving sync. Expired sessions require signing in again.

## Retrieve without pushing

Cloud checkout: `/workspace/Simple`. All changes and commits remain there. Download the provided Git bundle and clone it locally with `git clone Simple.bundle Simple`, or download the source archive and APK. A bundle preserves commit history; a source ZIP does not. No branch publication, push or PR is required.
