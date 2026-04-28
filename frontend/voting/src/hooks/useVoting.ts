import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { votingService } from "../services/votingService";
import { type VoteRequest } from "../types";

export const useAgendas = () => {
  return useQuery({
    queryKey: ["agendas"],
    queryFn: votingService.getAgendas,
  });
};

export const useAgendaDetails = (id: number) => {
  return useQuery({
    queryKey: ["agenda", id],
    queryFn: () => votingService.getAgendaDetails(id),
    refetchInterval: (query) => (query.state.data?.status === "OPEN" ? 3000 : false),
  });
};

export const useCreateAgenda = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ title, description }: { title: string; description: string }) =>
      votingService.createAgenda(title, description),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["agendas"] });
    },
  });
};

export const useVote = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ sessionId, vote }: { sessionId: number; vote: VoteRequest }) =>
      votingService.registerVote(sessionId, vote),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: ["agendas"] });
      console.log(`Voto registrado para sessão ${variables.sessionId}`);
    },
  });
};

export const useOpenSession = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ agendaId, durationInMinutes }: { agendaId: number; durationInMinutes: number }) =>
      votingService.openSession(agendaId, durationInMinutes),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: ["agendas"] });
      queryClient.invalidateQueries({
        queryKey: ["agenda", variables.agendaId],
      });
    },
  });
};
