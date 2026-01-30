import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    stages: [
        { duration: '30s', target: 20 }, // ramp-up to 20 users
        { duration: '1m', target: 20 },  // stay at 20 users
        { duration: '30s', target: 0 },  // ramp-down
    ],
    thresholds: {
        http_req_duration: ['p(95)<500'], // 95% of requests must be below 500ms
    },
};

const BASE_URL = __ENV.BASE_URL || 'http://localhost:9880';
const TENANT_ID = 'vibe-afrika';

export default function () {
    // 1. Create a profile
    const payload = JSON.stringify({
        handle: `user_${Math.floor(Math.random() * 1000000)}`,
        attributes: {
            source: 'performance_test',
            region: 'West Africa'
        }
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
            'X-Tenant-Id': TENANT_ID,
        },
    };

    const createRes = http.post(`${BASE_URL}/api/v1/profiles`, payload, params);
    check(createRes, {
        'create profile status is 201': (r) => r.status === 201,
    });

    if (createRes.status === 201) {
        const profileId = createRes.json().id;

        // 2. Fetch the profile (via Gateway routing)
        const getRes = http.get(`${BASE_URL}/api/v1/profiles/${profileId}`, params);
        check(getRes, {
            'get profile status is 200': (r) => r.status === 200,
        });
    }

    sleep(1);
}
