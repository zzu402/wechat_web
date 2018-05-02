
function checkErrorDatas(data,httpOptions,errorCallback) {
    if (data.code == "success") {
        return true;
    }else{
        alert(data.errorMsg);
    }
    return false;

}
