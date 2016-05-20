var GET_JSON_ERROR_MESSAGE = "Помилка сервера при виконанні методу $.getJSON(...\n" +
    "Неможливо одержати дані.\n" +
    "Будь-ласка, зверніться до адміністратора системи.";

function JSONToUserReport(json, testStatus) {
    if (testStatus != "success") {
        alert(GET_JSON_ERROR_MESSAGE);
        return;
    }
    var JSONstr = JSON.stringify(json);
    userReport = JSON.parse(JSONstr, dateReviver);
}

function dateReviver(key, value) {
    if (key.search("date") > -1 && typeof value === 'number') {

        return new Date(value);
    } else {
        return value;
    }
}

function JSONToList (json, testStatus) {
    if (testStatus != "success") {
        alert(GET_JSON_ERROR_MESSAGE);
        return;
    }
    tempList = json;
}

