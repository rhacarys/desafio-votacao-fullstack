import { useParams, useNavigate } from "react-router-dom";
import { Box, Container, Button, Card, CircularProgress, Alert } from "@mui/material";
import ArrowBackIcon from "@mui/icons-material/ArrowBack";
import { useAgendaDetails } from "../hooks/useVoting";
import { AgendaHeader } from "../components/AgendaDetails/AgendaHeader";
import { AgendaPendingState } from "../components/AgendaDetails/AgendaPendingState";
import { AgendaOpenState } from "../components/AgendaDetails/AgendaOpenState";
import { AgendaClosedState } from "../components/AgendaDetails/AgendaClosedState";

export function AgendaDetails() {
  const { id } = useParams();
  const navigate = useNavigate();

  const { data: agenda, isLoading, isError } = useAgendaDetails(Number(id));

  if (isLoading) {
    return (
      <Box sx={{ display: "flex", justifyContent: "center", mt: 10 }}>
        <CircularProgress />
      </Box>
    );
  }

  if (isError || !agenda) {
    return (
      <Container maxWidth="md" sx={{ py: 8, textAlign: "center" }}>
        <Alert severity="error">Erro ao carregar os detalhes da pauta.</Alert>
      </Container>
    );
  }

  return (
    <Container maxWidth="md" sx={{ py: 4 }}>
      <Button
        startIcon={<ArrowBackIcon />}
        onClick={() => navigate("/")}
        sx={{ mb: 4, color: "text.secondary", textTransform: "none", fontWeight: "medium" }}
      >
        Voltar para o Dashboard
      </Button>

      <Card elevation={0} sx={{ borderRadius: 3, border: "1px solid", borderColor: "divider", overflow: "hidden" }}>
        <AgendaHeader title={agenda.title} description={agenda.description} status={agenda.status} />

        <Box sx={{ p: { xs: 4, md: 5 }, bgcolor: "grey.50" }}>
          {agenda.status === "PENDING" && <AgendaPendingState agendaId={agenda.id} />}

          {agenda.status === "OPEN" && agenda.closesAt && agenda.sessionId && (
            <AgendaOpenState sessionId={agenda.sessionId} closesAt={agenda.closesAt} />
          )}

          {agenda.status === "CLOSED" && (
            <AgendaClosedState yesVotes={agenda.yesVotes} noVotes={agenda.noVotes} totalVotes={agenda.totalVotes} />
          )}
        </Box>
      </Card>
    </Container>
  );
}
