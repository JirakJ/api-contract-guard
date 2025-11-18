// GraphQL API calls - Examples for Kei plugin (v0.2)
// These will be validated in v0.2 release

import { gql } from 'graphql-tag';

// ✓ VALID: Query all users
const GET_USERS = gql`
  query GetUsers {
    users {
      id
      name
      email
      role
    }
  }
`;

// ✓ VALID: Query user by ID
const GET_USER = gql`
  query GetUser($id: ID!) {
    user(id: $id) {
      id
      name
      email
      role
      createdAt
    }
  }
`;

// ✓ VALID: Create user mutation
const CREATE_USER = gql`
  mutation CreateUser($input: CreateUserInput!) {
    createUser(input: $input) {
      id
      name
      email
      role
    }
  }
`;

// Example usage with variables
async function createUser() {
  const variables = {
    input: {
      name: 'John Doe',
      email: 'john@example.com',
      role: 'USER'
    }
  };
  
  // This will be validated against schema.graphql in v0.2
  const response = await fetch('/graphql', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      query: CREATE_USER,
      variables
    })
  });
  
  return response.json();
}

// ❌ INVALID: Missing required field (coming in v0.2)
async function createUserInvalid() {
  const variables = {
    input: {
      name: 'Jane Doe',
      role: 'ADMIN'
      // Missing: email (required field)
    }
  };
  
  const response = await fetch('/graphql', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      query: CREATE_USER,
      variables
    })
  });
  
  return response.json();
}

// ❌ INVALID: Wrong enum value (coming in v0.2)
async function createUserWrongRole() {
  const variables = {
    input: {
      name: 'Bob Smith',
      email: 'bob@example.com',
      role: 'SUPERADMIN' // Should be: ADMIN, USER, or GUEST
    }
  };
  
  const response = await fetch('/graphql', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      query: CREATE_USER,
      variables
    })
  });
  
  return response.json();
}

// ✓ VALID: Update user
const UPDATE_USER = gql`
  mutation UpdateUser($id: ID!, $input: UpdateUserInput!) {
    updateUser(id: $id, input: $input) {
      id
      name
      email
      updatedAt
    }
  }
`;

// ✓ VALID: Delete user
const DELETE_USER = gql`
  mutation DeleteUser($id: ID!) {
    deleteUser(id: $id)
  }
`;

export {
  GET_USERS,
  GET_USER,
  CREATE_USER,
  UPDATE_USER,
  DELETE_USER,
  createUser,
  createUserInvalid,
  createUserWrongRole
};
