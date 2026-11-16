create table public.profiles (
 user_id uuid primary key references auth.users(id) on delete cascade,
 data jsonb not null default '{}'::jsonb,
 updated_at timestamptz not null default now()
);
alter table public.profiles enable row level security;
create policy "Read own profile" on public.profiles for select to authenticated using ((select auth.uid()) = user_id);
create policy "Insert own profile" on public.profiles for insert to authenticated with check ((select auth.uid()) = user_id);
create policy "Update own profile" on public.profiles for update to authenticated using ((select auth.uid()) = user_id) with check ((select auth.uid()) = user_id);
create policy "Delete own profile" on public.profiles for delete to authenticated using ((select auth.uid()) = user_id);
insert into storage.buckets(id,name,public,file_size_limit,allowed_mime_types) values ('documents','documents',false,20971520,array['application/pdf','application/vnd.openxmlformats-officedocument.wordprocessingml.document']);
create policy "Own documents" on storage.objects for all to authenticated using (bucket_id='documents' and (storage.foldername(name))[1]=(select auth.uid())::text) with check (bucket_id='documents' and (storage.foldername(name))[1]=(select auth.uid())::text);
