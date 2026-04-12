const TokenKey = "ADMIN-TOKEN";
const UserKey = "ADMIN-USER";

export function getToken() {
  return JSON.parse(window.localStorage.getItem(TokenKey));
}

export function setToken(token, user) {
  window.localStorage.setItem(TokenKey, JSON.stringify(token));
  if (user) {
    window.localStorage.setItem(UserKey, JSON.stringify(user));
  }
}

export function removeToken() {
  window.localStorage.removeItem(TokenKey);
  window.localStorage.removeItem(UserKey);
}

export function getUser() {
  return JSON.parse(window.localStorage.getItem(UserKey));
}

export function isLoggedIn() {
  const token = getToken();
  const user = getUser();
  return !!(token && user);
}

export function getRouter(){
  
}