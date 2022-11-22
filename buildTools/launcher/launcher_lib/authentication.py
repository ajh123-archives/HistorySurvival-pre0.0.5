import requests
import json
import uuid

token = uuid.uuid4()


class User:
    email: str
    properties = []


class Profile:
    id: str
    name: str


class Account:
    user: User
    accessToken: str
    clientToken: str
    selectedProfile: Profile
    availableProfiles = []


class LoginException(Exception):
    pass


def login(email: str, password: str) -> Account:
    body = {
        "agent": {
            "name": "History Survival",
            "version": 1
        },
        "username": email,
        "password": password,
        "clientToken": str(token),
        "requestUser": True
    }

    url = "https://minersonline.tk/api/authenticate/"

    headers = {'Content-Type': 'application/json'}
    r = requests.post(url, data=json.dumps(body), headers=headers)
    data: dict = json.loads(r.text)

    user = User()
    user.email = data["user"]["username"]
    user.properties = data["user"]["properties"]

    acc = Account()
    acc.user = user
    acc.accessToken = data["accessToken"]
    acc.clientToken = data["clientToken"]

    if "selectedProfile" not in data:
        raise LoginException("Account does not contain a profile!")

    acc.selectedProfile = Profile()
    acc.selectedProfile.id = data["selectedProfile"]["id"]
    acc.selectedProfile.name = data["selectedProfile"]["name"]

    for profileData in data["availableProfiles"]:
        profile = Profile()
        profile.id = profileData["id"]
        profile.name = profileData["name"]
        acc.availableProfiles.append(profile)

    return acc
