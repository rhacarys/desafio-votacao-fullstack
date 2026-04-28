import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  vus: 100, 
  duration: '30s',
};

export function setup() {
  const headers = { 'Content-Type': 'application/json' };

  const agendaRes = http.post('http://backend:8080/api/v1/agendas', JSON.stringify({
    title: "Pauta de Stress Test",
    description: "Descrição da pauta para teste de performance"
  }), { headers });

  const agendaId = JSON.parse(agendaRes.body).id;

  http.post('http://backend:8080/api/v1/sessions/open', JSON.stringify({
    agendaId: agendaId,
    durationInMinutes: 10
  }), { headers });

  return { sessionId: agendaId };
}

export default function (data) {
  const url = `http://backend:8080/api/v1/sessions/${data.sessionId}/votes`;
  
  const fakeCpf = Math.floor(10000000000 + Math.random() * 90000000000).toString();

  const payload = JSON.stringify({
    associateCpf: fakeCpf,
    choice: Math.random() > 0.5 ? 'YES' : 'NO',
  });

  const params = { headers: { 'Content-Type': 'application/json' } };
  const res = http.post(url, payload, params);

  check(res, { 
    'Vote registered, duplicated or CPF unable to vote': (r) => [202, 404, 422].includes(r.status) 
  });
  
  sleep(0.1);
}