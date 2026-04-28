import { Box, Stack, Typography, LinearProgress, Divider } from "@mui/material";
import BarChartIcon from "@mui/icons-material/BarChart";

interface AgendaClosedStateProps {
  yesVotes: number;
  noVotes: number;
  totalVotes: number;
}

export function AgendaClosedState({ yesVotes, noVotes, totalVotes }: AgendaClosedStateProps) {
  const yesPercentage = totalVotes > 0 ? Math.round((yesVotes / totalVotes) * 100) : 0;
  const noPercentage = totalVotes > 0 ? Math.round((noVotes / totalVotes) * 100) : 0;

  return (
    <Box sx={{ maxWidth: 500, mx: "auto", py: 2 }}>
      <Stack direction="row" spacing={1} sx={{ alignItems: "center", justifyContent: "center", mb: 4 }}>
        <BarChartIcon sx={{ color: "primary.main", fontSize: 32 }} />
        <Typography variant="h5" sx={{ fontWeight: "bold" }}>
          Resultados Finais
        </Typography>
      </Stack>

      <Box sx={{ mb: 4 }}>
        <Stack direction="row" sx={{ justifyContent: "space-between", mb: 1 }}>
          <Typography sx={{ fontWeight: "bold", color: "success.main" }}>SIM ({yesVotes})</Typography>
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
          <Typography sx={{ fontWeight: "bold", color: "error.main" }}>NÃO ({noVotes})</Typography>
          <Typography sx={{ fontWeight: "bold" }}>{noPercentage}%</Typography>
        </Stack>
        <LinearProgress variant="determinate" value={noPercentage} color="error" sx={{ height: 12, borderRadius: 6 }} />
      </Box>

      <Divider sx={{ my: 3 }} />
      <Typography align="center" variant="subtitle1" sx={{ fontWeight: "bold", color: "text.secondary" }}>
        Total de votos: {totalVotes}
      </Typography>
    </Box>
  );
}
