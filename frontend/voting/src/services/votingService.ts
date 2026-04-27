import { api } from "./api";
import { type Agenda, type AgendaDetails, type VotingSession, type VoteRequest } from "../types";

export const votingService = {
  getAgendas: async () => {
    const response = await api.get<Agenda[]>("/agendas");
    return response.data;
  },

  getAgendaDetails: async (id: number) => {
    const response = await api.get<AgendaDetails>(`/agendas/${id}`);
    return response.data;
  },

  createAgenda: async (title: string, description: string) => {
    const response = await api.post<Agenda>("/agendas", { title, description });
    return response.data;
  },

  openSession: async (agendaId: number, durationInMinutes: number = 1) => {
    const response = await api.post<VotingSession>("/sessions/open", {
      agendaId,
      durationInMinutes,
    });
    return response.data;
  },

  registerVote: async (sessionId: number, vote: VoteRequest) => {
    const response = await api.post(`/sessions/${sessionId}/votes`, vote);
    return response.data;
  },
};
