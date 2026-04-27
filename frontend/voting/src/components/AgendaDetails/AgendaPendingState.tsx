import { useState } from "react";
import { Stack, Typography, Box, TextField, Button, CircularProgress } from "@mui/material";
import PlayArrowIcon from "@mui/icons-material/PlayArrow";
import { useOpenSession } from "../../hooks/useVoting";
import { useGlobalSnackbar } from "../../contexts/SnackbarContext";

interface AgendaPendingStateProps {
  agendaId: number;
}

export function AgendaPendingState({ agendaId }: AgendaPendingStateProps) {
  const { showSnackbar } = useGlobalSnackbar();
  const [duration, setDuration] = useState("1");
  const { mutate: openSession, isPending } = useOpenSession();

  const handleOpenSession = () => {
    const durationNum = parseInt(duration, 10);
    openSession(
      {
        agendaId,
        durationInMinutes: isNaN(durationNum) || durationNum < 1 ? 1 : durationNum,
      },
      {
        onSuccess: () => {
          showSnackbar("Sessão de votação iniciada!", "success");
        },
        onError: (error: any) => {
          showSnackbar(error.response?.data?.message || "Erro ao iniciar a sessão. Tente novamente.", "error");
        },
      },
    );
  };

  return (
    <Stack spacing={3} sx={{ alignItems: "center", py: 2 }}>
      <PlayArrowIcon sx={{ fontSize: 56, color: "primary.main" }} />
      <Typography variant="h6" sx={{ fontWeight: "bold" }}>
        Iniciar Votação
      </Typography>

      <Box sx={{ width: "100%", maxWidth: 300 }}>
        <TextField
          fullWidth
          label="Duração em Minutos"
          variant="outlined"
          type="number"
          value={duration}
          onChange={(e) => setDuration(e.target.value)}
          slotProps={{ htmlInput: { min: 1 } }}
          sx={{ mb: 3, bgcolor: "background.paper" }}
        />
        <Button
          fullWidth
          variant="contained"
          size="large"
          onClick={handleOpenSession}
          disabled={isPending}
          sx={{ py: 1.5, fontWeight: "bold", borderRadius: 2 }}
        >
          {isPending ? <CircularProgress size={26} color="inherit" /> : "Abrir Sessão"}
        </Button>
      </Box>
    </Stack>
  );
}
