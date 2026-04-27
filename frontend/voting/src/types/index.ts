export interface Agenda {
  id: number;
  title: string;
  description: string;
}

export interface AgendaDetails {
  id: number;
  title: string;
  description: string;
  status: "PENDING" | "OPEN" | "CLOSED";
  sessionId: number | null;
  opensAt: string | null;
  closesAt: string | null;
  yesVotes: number;
  noVotes: number;
  totalVotes: number;
}

export interface VotingSession {
  sessionId: number;
  agendaId: number;
  opensAt: string;
  closesAt: string;
}

export interface VoteRequest {
  associateCpf: string;
  choice: "YES" | "NO";
}
