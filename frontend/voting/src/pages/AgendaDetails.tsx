import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { useAgendaDetails, useOpenSession, useVote } from "../hooks/useVoting";
import {
  Box,
  Container,
  Typography,
  Button,
  Card,
  TextField,
  Stack,
  Alert,
  CircularProgress,
  Grid,
  Divider,
  LinearProgress,
  Paper,
} from "@mui/material";
import ArrowBackIcon from "@mui/icons-material/ArrowBack";
import PlayArrowIcon from "@mui/icons-material/PlayArrow";
import CheckCircleIcon from "@mui/icons-material/CheckCircle";
import HighlightOffIcon from "@mui/icons-material/HighlightOff";
import BarChartIcon from "@mui/icons-material/BarChart";

export function AgendaDetails() {
  const { id } = useParams();
  const navigate = useNavigate();

  // Busca os detalhes unificados da pauta (com auto-refresh se estiver OPEN)
  const { data: agenda, isLoading, isError } = useAgendaDetails(Number(id));
  const { mutate: openSession, isPending: isOpening } = useOpenSession();
  const { mutate: registerVote, isPending: isVoting } = useVote();

  // Estados locais
  const [duration, setDuration] = useState("1"); // Padrão de 1 minuto
  const [cpf, setCpf] = useState("");
  const [feedback, setFeedback] = useState<{
    type: "success" | "error";
    text: string;
  } | null>(null);

  const [timeLeft, setTimeLeft] = useState<number | null>(null);

  useEffect(() => {
    if (agenda?.status === "OPEN" && agenda?.closesAt) {
      const calculateTimeLeft = () => {
        const closeTime = new Date(agenda.closesAt || 0).getTime();
        const now = new Date().getTime();
        const difference = closeTime - now;

        if (difference <= 0) {
          setTimeLeft(0);
          return;
        }

        setTimeLeft(Math.floor(difference / 1000));
      };

      calculateTimeLeft();

      const timer = setInterval(calculateTimeLeft, 1000);

      return () => clearInterval(timer);
    }
  }, [agenda?.status, agenda?.closesAt]);

  const formatTime = (seconds: number) => {
    const m = Math.floor(seconds / 60);
    const s = seconds % 60;
    return `${m.toString().padStart(2, "0")}:${s.toString().padStart(2, "0")}`;
  };

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

  const handleOpenSession = () => {
    setFeedback(null);
    const durationNum = parseInt(duration, 10);
    openSession(
      {
        agendaId: agenda.id,
        durationInMinutes: isNaN(durationNum) || durationNum < 1 ? 1 : durationNum,
      },
      {
        onSuccess: () =>
          setFeedback({
            type: "success",
            text: "Sessão iniciada com sucesso!",
          }),
        onError: (error: any) =>
          setFeedback({
            type: "error",
            text: error.response?.data?.message || "Erro ao abrir.",
          }),
      },
    );
  };

  const handleVote = (choice: "YES" | "NO") => {
    if (!agenda.sessionId) return;
    if (cpf.length !== 11) {
      setFeedback({
        type: "error",
        text: "O CPF deve ter exatamente 11 dígitos numéricos.",
      });
      return;
    }

    setFeedback(null);
    registerVote(
      { sessionId: agenda.sessionId, vote: { associateCpf: cpf, choice } },
      {
        onSuccess: () => {
          setFeedback({ type: "success", text: `Voto computado com sucesso!` });
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

  // Cálculos para a barra de resultados
  const yesPercentage = agenda.totalVotes > 0 ? Math.round((agenda.yesVotes / agenda.totalVotes) * 100) : 0;
  const noPercentage = agenda.totalVotes > 0 ? Math.round((agenda.noVotes / agenda.totalVotes) * 100) : 0;

  return (
    <Container maxWidth="md" sx={{ py: 4 }}>
      <Button
        startIcon={<ArrowBackIcon />}
        onClick={() => navigate("/")}
        sx={{
          mb: 4,
          color: "text.secondary",
          textTransform: "none",
          fontWeight: "medium",
        }}
      >
        Voltar para o Dashboard
      </Button>

      <Card
        elevation={0}
        sx={{
          borderRadius: 3,
          border: "1px solid",
          borderColor: "divider",
          overflow: "hidden",
        }}
      >
        {/* Cabeçalho */}
        <Box
          sx={{
            p: { xs: 4, md: 5 },
            borderBottom: "1px solid",
            borderColor: "grey.100",
          }}
        >
          <Stack
            direction="row"
            sx={{
              justifyContent: "space-between",
              alignItems: "flex-start",
              mb: 2,
            }}
          >
            <Typography variant="h4" sx={{ fontWeight: "bold", color: "text.primary" }}>
              {agenda.title}
            </Typography>
            <Paper
              elevation={0}
              sx={{
                px: 2,
                py: 0.5,
                bgcolor:
                  agenda.status === "OPEN"
                    ? "success.light"
                    : agenda.status === "CLOSED"
                      ? "grey.200"
                      : "warning.light",
                color: agenda.status === "OPEN" ? "success.dark" : "text.secondary",
                fontWeight: "bold",
                borderRadius: 2,
              }}
            >
              {agenda.status === "PENDING" ? "Aguardando" : agenda.status === "OPEN" ? "Votação Aberta" : "Encerrada"}
            </Paper>
          </Stack>
          <Typography variant="subtitle1" sx={{ color: "text.secondary", lineHeight: 1.6 }}>
            {agenda.description}
          </Typography>
        </Box>

        {/* Área Dinâmica baseada no Status */}
        <Box sx={{ p: { xs: 4, md: 5 }, bgcolor: "grey.50" }}>
          {feedback && (
            <Alert severity={feedback.type} sx={{ mb: 4 }}>
              {feedback.text}
            </Alert>
          )}

          {/* ESTADO 1: PENDING (Abrir Sessão) */}
          {agenda.status === "PENDING" && (
            <Stack spacing={3} sx={{ alignItems: "center", py: 2 }}>
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
                  disabled={isOpening}
                  sx={{ py: 1.5, fontWeight: "bold", borderRadius: 2 }}
                >
                  {isOpening ? <CircularProgress size={26} color="inherit" /> : "Abrir Sessão"}
                </Button>
              </Box>
            </Stack>
          )}

          {/* ESTADO 2: OPEN (Votar) */}
          {agenda.status === "OPEN" && (
            <Box sx={{ maxWidth: 400, mx: "auto" }}>
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
                    disabled={isVoting || cpf.length !== 11}
                    sx={{
                      py: 1.5,
                      borderWidth: 2,
                      fontWeight: "bold",
                      "&:hover": { borderWidth: 2 },
                    }}
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
                    disabled={isVoting || cpf.length !== 11}
                    sx={{
                      py: 1.5,
                      borderWidth: 2,
                      fontWeight: "bold",
                      "&:hover": { borderWidth: 2 },
                    }}
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
                  <Typography
                    variant="h5"
                    sx={{
                      fontWeight: "bold",
                      color: timeLeft <= 10 ? "error.main" : "text.primary",
                    }}
                  >
                    {formatTime(timeLeft)}
                  </Typography>
                </Box>
              )}
            </Box>
          )}

          {/* ESTADO 3: CLOSED (Resultados) */}
          {agenda.status === "CLOSED" && (
            <Box sx={{ maxWidth: 500, mx: "auto", py: 2 }}>
              <Stack direction="row" spacing={1} sx={{ alignItems: "center", justifyContent: "center", mb: 4 }}>
                <BarChartIcon sx={{ color: "primary.main", fontSize: 32 }} />
                <Typography variant="h5" sx={{ fontWeight: "bold" }}>
                  Resultados Finais
                </Typography>
              </Stack>

              <Box sx={{ mb: 4 }}>
                <Stack direction="row" sx={{ justifyContent: "space-between", mb: 1 }}>
                  <Typography sx={{ fontWeight: "bold", color: "success.main" }}>SIM ({agenda.yesVotes})</Typography>
                  <Typography sx={{ fontWeight: "bold" }}>{yesPercentage}%</Typography>
                </Stack>
                <LinearProgress
                  variant="determinate"
                  value={yesPercentage}
                  color="success"
                  sx={{ height: 12, borderRadius: 6 }}
                />
              </Box>

              <Box sx={{ mb: 4 }}>
                <Stack direction="row" sx={{ justifyContent: "space-between", mb: 1 }}>
                  <Typography sx={{ fontWeight: "bold", color: "error.main" }}>NÃO ({agenda.noVotes})</Typography>
                  <Typography sx={{ fontWeight: "bold" }}>{noPercentage}%</Typography>
                </Stack>
                <LinearProgress
                  variant="determinate"
                  value={noPercentage}
                  color="error"
                  sx={{ height: 12, borderRadius: 6 }}
                />
              </Box>

              <Divider sx={{ my: 3 }} />
              <Typography align="center" variant="subtitle1" sx={{ fontWeight: "bold", color: "text.secondary" }}>
                Total de votos: {agenda.totalVotes}
              </Typography>
            </Box>
          )}
        </Box>
      </Card>
    </Container>
  );
}
