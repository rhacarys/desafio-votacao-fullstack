const errorDictionary: Record<string, string> = {
  DUPLICATE_VOTE: "Este CPF já registrou um voto nesta pauta.",
  SESSION_CLOSED: "A sessão de votação já foi encerrada.",
  AGENDA_NOT_FOUND: "A pauta solicitada não foi encontrada.",
  SESSION_ALREADY_EXISTS: "Esta pauta já possui uma sessão de votação (aberta ou encerrada).",
  NETWORK_ERROR: "Erro de conexão. Verifique sua internet.",
  UNKNOWN_ERROR: "Ocorreu um erro inesperado. Tente novamente mais tarde.",
};

export function getErrorMessage(errorCode?: string): string {
  if (!errorCode) return errorDictionary["UNKNOWN_ERROR"];
  return errorDictionary[errorCode] || errorDictionary["UNKNOWN_ERROR"];
}
