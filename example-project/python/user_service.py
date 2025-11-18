"""
Python API calls - Examples for Kei plugin (v0.2)
These will be validated in v0.2 release
"""

import requests
import httpx
from typing import Dict, Any

BASE_URL = "http://localhost:3000"


# ✓ VALID: Create user with all required fields
def create_user_valid():
    response = requests.post(
        f"{BASE_URL}/api/users",
        json={
            "name": "John Doe",
            "email": "john@example.com",
            "role": "user"
        }
    )
    return response.json()


# ❌ INVALID: Missing required field 'email' (coming in v0.2)
def create_user_missing_email():
    response = requests.post(
        f"{BASE_URL}/api/users",
        json={
            "name": "Jane Doe",
            "role": "admin"
            # Missing: email
        }
    )
    return response.json()


# ❌ INVALID: Wrong enum value (coming in v0.2)
def create_user_wrong_role():
    response = requests.post(
        f"{BASE_URL}/api/users",
        json={
            "name": "Bob Smith",
            "email": "bob@example.com",
            "role": "superadmin"  # Should be: admin, user, or guest
        }
    )
    return response.json()


# ✓ VALID: Get all users
def get_users():
    response = requests.get(f"{BASE_URL}/api/users")
    return response.json()


# ✓ VALID: Get user by ID
def get_user_by_id(user_id: str):
    response = requests.get(f"{BASE_URL}/api/users/{user_id}")
    return response.json()


# ✓ VALID: Using httpx
async def create_user_httpx():
    async with httpx.AsyncClient() as client:
        response = await client.post(
            f"{BASE_URL}/api/users",
            json={
                "name": "Alice Johnson",
                "email": "alice@example.com",
                "role": "user"
            }
        )
        return response.json()


# ✓ VALID: Update user
def update_user(user_id: str):
    response = requests.put(
        f"{BASE_URL}/api/users/{user_id}",
        json={"name": "Updated Name"}
    )
    return response.json()


# ✓ VALID: Delete user
def delete_user(user_id: str):
    response = requests.delete(f"{BASE_URL}/api/users/{user_id}")
    return response.status_code == 204
