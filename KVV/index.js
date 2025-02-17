const axios = require('axios');

const STOP = {
    HAGSFELD_BAHNHOF: '7003102',
    HAGSFELD_SUD: '7000231',
    HAGSFELD_SCHAFERSTRASSE: '7003205',
    HAGSFELD_FACHERBAD: '7000405',
}

const LINE = {
    S2: {
        symbol: 'S2',
        stops: [
            {
                stop: STOP.HAGSFELD_BAHNHOF,
                toCenter: '3',
                fromCenter: '4',
            },
            {
                stop: STOP.HAGSFELD_SUD,
                toCenter: '1',
                fromCenter: '2',
            }
        ]
    },
    BUS_32: {
        symbol: '32',
        stops: [
            {
                stop: STOP.HAGSFELD_SUD,
                toCenter: 'A',
                fromCenter: 'B',
            },
            {
                stop: STOP.HAGSFELD_SCHAFERSTRASSE,
                toCenter: 'A',
                fromCenter: 'B',
            },
            {
                stop: STOP.HAGSFELD_FACHERBAD,
                toCenter: 'A',
                fromCenter: 'B',
            }
        ]
    },
    TRAM_4: {
        symbol: '4',
        stops: [
            {
                stop: STOP.HAGSFELD_FACHERBAD,
                toCenter: '1',
                fromCenter: '2',
            }
        ]
    }
}

const DIRECTION = {
    TO_CENTER: 'toCenter',
    FROM_CENTER: 'fromCenter',
}

async function fetchData(params) {
    try {
        const response = await axios.get('https://projekte.kvv-efa.de/sl3/XSLT_DM_REQUEST', {
            params: {
                outputFormat: 'JSON',
                language: 'en',
                stateless: '1',
                coordOutputFormat: 'WGS84[DD.ddddd]',
                type_dm: 'stop',
                itdTime: '0000',
                useRealtime: '1',
                mode: 'direct',
                limit: 1000,
                ...params
            },
            headers: { Accept: '*/*', 'User-Agent': 'Thunder Client (https://www.thunderclient.com)' }
        });
        return response.data;
    } catch (error) {
        console.error(error);
        return {};
    }
}



async function fetchDayTimeTable(date, stop, line, direction) {
    const strDate = formatDate(date);

    const params = {
        name_dm: stop,
        itdDate: strDate,
    };

    const json = await fetchData(params);

    const symbol = line.symbol;
    const platform = line.stops.find(item => item.stop === stop)[direction];
    json.departureList = json.departureList.filter(dep => dep.platform === platform && dep.servingLine.symbol === symbol);

    const item = json.departureList[0];
    return {
        stop: item.stopName,
        line: line.symbol,
        platform: platform,
        destination: direction,
        weekday: date.getDay() == 6 ? 'Saturday' : date.getDay() == 0 ? 'Sunday' : 'Weekday',
        timetable: json.departureList.map(dep => formatTime(dep.dateTime))
    }
}

async function getTimeTable(stop, line, direction) {
    const result = [];
    const today = new Date();
    for (let i = 0; i < 3; i++) {
        const date = addDays(today, i - 1 - today.getDay());
        const json = await fetchDayTimeTable(date, stop, line, direction);
        result.push(json);
    }

    return result;
}

function pad(num) {
    return String(num).padStart(2, '0');
}

function formatDate(date) {
    return `${date.getFullYear()}${pad(date.getMonth() + 1)}${pad(date.getDate())}`;
}

function formatTime(dateTime) {
    return `${pad(dateTime.hour)}:${pad(dateTime.minute)}`;
}

function addDays(date, days) {
    const result = new Date(date);
    result.setDate(result.getDate() + days);
    return result;
}

function csv(result) {
    const [sat, sun, weeday] = result;
    const lines = [];
    lines.push(sat.stop);
    lines.push(sat.line);
    lines.push(sat.destination);

    lines.push('WEEKDAY');
    lines.push(...weeday.timetable);
    lines.push('SAT');
    lines.push(...sat.timetable);
    lines.push('SUN');
    lines.push(...sun.timetable);

    return lines.join('\n');
}

(async () => {
    console.log('BEGIN');
    const result = await getTimeTable(STOP.HAGSFELD_BAHNHOF, LINE.S2, DIRECTION.TO_CENTER);
    console.log(result);
    console.log(csv(result));
})();

