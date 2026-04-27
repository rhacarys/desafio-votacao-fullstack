import { useState, useEffect } from "react";
import { Box, Typography, TextField, Grid, Button, Alert } from "@mui/material";
import CheckCircleIcon from "@mui/icons-material/CheckCircle";
import HighlightOffIcon from "@mui/icons-material/HighlightOff";
import { useVote } from "../../hooks/useVoting";

interface AgendaOpenStateProps {
  sessionId: number;
  closesAt: string;
}

export function AgendaOpenState({ sessionId, closesAt }: AgendaOpenStateProps) {
  const [cpf, setCpf] = useState("");
  const [timeLeft, setTimeLeft] = useState<number | null>(null);
  const [feedback, setFeedback] = useState<{ type: "success" | "error"; text: string } | null>(null);
  const { mutate: registerVote, isPending } = useVote();

  useEffect(() => {
    const calculateTimeLeft = () => {
      const closeTime = new Date(closesAt).getTime();
      const difference = closeTime - new Date().getTime();
      setTimeLeft(difference <= 0 ? 0 : Math.floor(difference / 1000));
    };

    calculateTimeLeft();
    const timer = setInterval(calculateTimeLeft, 1000);
    return () => clearInterval(timer);
  }, [closesAt]);

  const handleVote = (choice: "YES" | "NO") => {
    if (cpf.length !== 11) {
      setFeedback({ type: "error", text: "O CPF deve ter exatamente 11 dígitos numéricos." });
      return;
    }

    setFeedback(null);
    registerVote(
      { sessionId, vote: { associateCpf: cpf, choice } },
      {
        onSuccess: () => {
          setFeedback({ type: "success", text: "Voto computado com sucesso!" });
          setCpf("");
        },
        onError: (error: any) =>
          setFeedback({
            type: "error",
            text: error.response?.data?.message || "Erro ao votar.",
          }),
      },
    );
  };

  const formatTime = (seconds: number) => {
    const m = Math.floor(seconds / 60);
    const s = seconds % 60;
    return `${m.toString().padStart(2, "0")}:${s.toString().padStart(2, "0")}`;
  };

  return (
    <Box sx={{ maxWidth: 400, mx: "auto" }}>
      {feedback && (
        <Alert severity={feedback.type} sx={{ mb: 4 }}>
          {feedback.text}
        </Alert>
      )}

      <Typography variant="h6" align="center" sx={{ fontWeight: "bold", mb: 3 }}>
        Registre seu Voto
      </Typography>

      <TextField
        fullWidth
        label="CPF do Associado (Apenas números)"
        variant="outlined"
        value={cpf}
        onChange={(e) => setCpf(e.target.value.replace(/\D/g, ""))}
        slotProps={{ htmlInput: { maxLength: 11 } }}
        sx={{ mb: 4, bgcolor: "background.paper" }}
      />

      <Grid container spacing={2}>
        <Grid size={6}>
          <Button
            fullWidth
            variant="outlined"
            color="success"
            size="large"
            startIcon={<CheckCircleIcon />}
            onClick={() => handleVote("YES")}
            disabled={isPending || cpf.length !== 11}
            sx={{ py: 1.5, borderWidth: 2, fontWeight: "bold", "&:hover": { borderWidth: 2 } }}
          >
            SIM
          </Button>
        </Grid>
        <Grid size={6}>
          <Button
            fullWidth
            variant="outlined"
            color="error"
            size="large"
            startIcon={<HighlightOffIcon />}
            onClick={() => handleVote("NO")}
            disabled={isPending || cpf.length !== 11}
            sx={{ py: 1.5, borderWidth: 2, fontWeight: "bold", "&:hover": { borderWidth: 2 } }}
          >
            NÃO
          </Button>
        </Grid>
      </Grid>

      {timeLeft !== null && (
        <Box sx={{ mt: 4, textAlign: "center" }}>
          <Typography variant="body2" sx={{ color: "text.secondary", mb: 1 }}>
            A sessão será encerrada em:
          </Typography>
          <Typography variant="h5" sx={{ fontWeight: "bold", color: timeLeft <= 10 ? "error.main" : "text.primary" }}>
            {formatTime(timeLeft)}
          </Typography>
        </Box>
      )}
    </Box>
  );
}
