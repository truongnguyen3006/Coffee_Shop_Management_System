# SQL files

- `schema.sql`: clean database structure, indexes, and constraints.
- `seed.sql`: public-friendly demo seed with lightweight sample data and known demo accounts.
- `seed-lite.sql`: same lightweight demo seed for quick local setup.
- `legacy/seed-full-from-original.sql`: full extracted seed from the original dump. This file is large and may contain old local sample data, so avoid publishing it in a public repository if privacy or repository size matters.
- `legacy/coffee-original-dump.sql`: original SQL dump kept for local reference.

## Recommended import order

### Portfolio / public demo setup
1. Create database `coffee`
2. Import `schema.sql`
3. Import `seed.sql`

### Full local setup from original dump
1. Create database `coffee`
2. Import `schema.sql`
3. Import `legacy/seed-full-from-original.sql`
