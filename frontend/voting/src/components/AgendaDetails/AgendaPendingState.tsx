import { useState } from "react";
import { Stack, Typography, Box, TextField, Button, CircularProgress, Alert } from "@mui/material";
import PlayArrowIcon from "@mui/icons-material/PlayArrow";
import { useOpenSession } from "../../hooks/useVoting";

interface AgendaPendingStateProps {
  agendaId: number;
}

export function AgendaPendingState({ agendaId }: AgendaPendingStateProps) {
  const [duration, setDuration] = useState("1");
  const [feedback, setFeedback] = useState<{ type: "error"; text: string } | null>(null);
  const { mutate: openSession, isPending } = useOpenSession();

  const handleOpenSession = () => {
    setFeedback(null);
    const durationNum = parseInt(duration, 10);
    openSession(
      {
        agendaId,
        durationInMinutes: isNaN(durationNum) || durationNum < 1 ? 1 : durationNum,
      },
      {
        onError: (error: any) =>
          setFeedback({
            type: "error",
            text: error.response?.data?.message || "Erro ao abrir sessão.",
          }),
      },
    );
  };

  return (
    <Stack spacing={3} sx={{ alignItems: "center", py: 2 }}>
      {feedback && <Alert severity={feedback.type}>{feedback.text}</Alert>}

      <PlayArrowIcon sx={{ fontSize: 56, color: "primary.main" }} />
      <Typography variant="h6" sx={{ fontWeight: "bold" }}>
        Iniciar Votação
      </Typography>

      <Box sx={{ width: "100%", maxWidth: 300 }}>
        <TextField
          fullWidth
          label="Duração em Minutos (Opcional)"
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
