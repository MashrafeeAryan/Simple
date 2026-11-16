# Supabase setup
Create a Supabase project and execute migrations/001_profiles.sql in its SQL editor. Enable email/password authentication and email confirmation. Configure your SMTP provider for production. In Simple → Account enter the HTTPS project URL and public publishable (or anon) key. Never embed service-role credentials.

No project credentials were supplied in Cloud. Live sign-in, cross-account isolation and storage remain unverified until this migration is deployed and tested against two users. Device data is private to the Android app; cloud data is protected by RLS, not end-to-end encrypted. Employer portal cookies stay in WebView and are not synced.
