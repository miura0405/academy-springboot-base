INSERT INTO categories (name, created_at, updated_at)
VALUES
  ('バックエンド', now(), now()),
  ('フロントエンド', now(), now()),
  ('インフラ', now(), now())
ON CONFLICT (name) DO NOTHING;
