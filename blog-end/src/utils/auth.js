const TokenKey = "ADMIN-TOKEN";

export function getToken() {
  return JSON.parse(window.sessionStorage.getItem(TokenKey));
}

export function setToken(token, rememberMe) {

  window.sessionStorage.setItem(TokenKey,JSON.stringify(token));
}

export function removeToken() {
  return window.sessionStorage.removeItem(TokenKey);
}

export function getRouter(){
  
}