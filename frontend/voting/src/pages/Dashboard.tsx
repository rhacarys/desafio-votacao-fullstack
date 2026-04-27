import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAgendas } from "../hooks/useVoting";
import { Alert, Box, CircularProgress, Grid } from "@mui/material";
import { DashboardHeader } from "../components/Dashboard/DashboardHeader";
import { DashboardEmptyState } from "../components/Dashboard/DashboardEmptyState";
import { AgendaCard } from "../components/Dashboard/AgendaCard";
import { CreateAgendaDialog } from "../components/Dashboard/CreateAgendaDialog";

export function Dashboard() {
  const navigate = useNavigate();
  const { data: agendas, isLoading, isError } = useAgendas();

  const [isDialogOpen, setIsDialogOpen] = useState(false);

  if (isLoading) {
    return (
      <Box sx={{ display: "flex", justifyContent: "center", alignItems: "center", height: 256 }}>
        <CircularProgress size={48} />
      </Box>
    );
  }

  if (isError) {
    return (
      <Alert severity="error" sx={{ borderRadius: 2 }}>
        Não foi possível conectar ao servidor. Verifique se a aplicação está rodando.
      </Alert>
    );
  }

  return (
    <Box>
      <DashboardHeader onOpenCreateDialog={() => setIsDialogOpen(true)} />

      {!agendas || agendas.length === 0 ? (
        <DashboardEmptyState />
      ) : (
        <Grid container spacing={3}>
          {agendas.map((agenda) => (
            <AgendaCard key={agenda.id} agenda={agenda} onClick={() => navigate(`/agenda/${agenda.id}`)} />
          ))}
        </Grid>
      )}

      <CreateAgendaDialog open={isDialogOpen} onClose={() => setIsDialogOpen(false)} />
    </Box>
  );
}
