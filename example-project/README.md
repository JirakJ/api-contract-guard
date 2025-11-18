# Kei Plugin - Example Project

This is an example project to test the Kei API Contract Testing plugin.

## Files

- `api/openapi.yaml` - OpenAPI 3.0 specification for User Management API
- `userService.ts` - TypeScript file with various API calls (valid and invalid)

## How to test

1. Open this project in IntelliJ IDEA / WebStorm with Kei plugin installed
2. Kei will automatically discover `api/openapi.yaml`
3. Open `userService.ts`
4. You should see:
   - ✅ Green for valid API calls
   - ❌ Red underlines for invalid API calls (missing fields, wrong types)
5. Use Alt+Enter on errors to see quick fixes

## Expected Violations

### In `createUserMissingEmail()`:
- Missing required field: `email`
- Quick fix available: Add missing field

### In `createUserWrongRole()`:
- Invalid enum value: `role` should be one of: admin, user, guest

### In `createUserAxiosMissingField()`:
- Missing required fields: `email`, `role`

## Contract Explorer

1. Open Tool Window: View → Tool Windows → Kei Contracts
2. You should see:
   - openapi.yaml (OPENAPI)
     - GET /api/users
     - POST /api/users
     - GET /api/users/{id}
     - PUT /api/users/{id}
     - DELETE /api/users/{id}

## Settings

Settings → Tools → Kei API Contract Testing
- Enable/disable validation
- Configure contract discovery patterns
