import { createClient } from "@supabase/supabase-js";

// ============================================================================
// 🔑 SUPABASE CONFIGURATION
// Auto-sanitizes the Project URL to avoid PGRST125 "Invalid path specified" errors.
// ============================================================================

// 👉 Supabase Project Base URL (MUST NOT include /rest/v1):
const RAW_URL = "https://eutppbbwkdbuydjqdfqz.supabase.co";

// Auto-clean any trailing slashes or accidental /rest/v1
const SUPABASE_URL = RAW_URL.replace(/\/rest\/v1\/?$/, '').replace(/\/+$/, '');

// 👉 Supabase Publishable / Anon Public Key:
const SUPABASE_PUBLIC_KEY = "sb_publishable_F2SviEJfy2Z3VTGyna2cZA_xaOpnRUY";

// Initialize and export a single Supabase client instance
export const supabase = createClient(SUPABASE_URL, SUPABASE_PUBLIC_KEY);

if (typeof window !== 'undefined') {
  window.supabase = supabase;
}
