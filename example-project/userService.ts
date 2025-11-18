// Example TypeScript file with API calls
// Kei plugin will validate these against openapi.yaml

// ✓ VALID: All required fields present
async function createValidUser() {
  const response = await fetch('/api/users', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      name: 'John Doe',
      email: 'john@example.com',
      role: 'user'
    })
  });
  return response.json();
}

// ❌ INVALID: Missing required field 'email'
async function createUserMissingEmail() {
  const response = await fetch('/api/users', {
    method: 'POST',
    body: JSON.stringify({
      name: 'Jane Doe',
      role: 'admin'
      // Missing: email
    })
  });
  return response.json();
}

// ❌ INVALID: Wrong enum value for 'role'
async function createUserWrongRole() {
  const response = await fetch('/api/users', {
    method: 'POST',
    body: JSON.stringify({
      name: 'Bob Smith',
      email: 'bob@example.com',
      role: 'superadmin' // Should be: admin, user, or guest
    })
  });
  return response.json();
}

// ✓ VALID: GET request (no body required)
async function getUsers() {
  const response = await fetch('/api/users');
  return response.json();
}

// ✓ VALID: GET with path parameter
async function getUserById(id: string) {
  const response = await fetch(`/api/users/${id}`);
  return response.json();
}

// ✓ VALID: Using axios
import axios from 'axios';

async function createUserWithAxios() {
  const response = await axios.post('/api/users', {
    name: 'Alice Johnson',
    email: 'alice@example.com',
    role: 'user'
  });
  return response.data;
}

// ❌ INVALID: Axios missing required field
async function createUserAxiosMissingField() {
  const response = await axios.post('/api/users', {
    name: 'Charlie Brown'
    // Missing: email, role
  });
  return response.data;
}

// ✓ VALID: Update user (all fields optional)
async function updateUser(id: string) {
  const response = await fetch(`/api/users/${id}`, {
    method: 'PUT',
    body: JSON.stringify({
      name: 'Updated Name'
    })
  });
  return response.json();
}

// ✓ VALID: Delete user (no body)
async function deleteUser(id: string) {
  const response = await fetch(`/api/users/${id}`, {
    method: 'DELETE'
  });
  return response.status === 204;
}

export {
  createValidUser,
  createUserMissingEmail,
  createUserWrongRole,
  getUsers,
  getUserById,
  createUserWithAxios,
  createUserAxiosMissingField,
  updateUser,
  deleteUser
};
