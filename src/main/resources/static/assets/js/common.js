function getCacheFromSessionStorage(key) {
    var cacheStr = sessionStorage.getItem(key);
    if (cacheStr)
        return JSON.parse(cacheStr);
    return null;
}

function setCache2SessionStorage(key, value) {
    if (value)
        sessionStorage.setItem(key, JSON.stringify(value));
    else
        sessionStorage.removeItem(key);
}

function removeCacheFromSessionStorage(key) {
    sessionStorage.removeItem(key);
}
