import { useNavigate } from "react-router-dom";
import { useAgendas } from "../hooks/useVoting";
import {
  Box,
  Button,
  Typography,
  CircularProgress,
  Alert,
  Grid,
  Card,
  CardContent,
  CardActions,
  Stack,
  Paper,
} from "@mui/material";
import AddIcon from "@mui/icons-material/Add";
import ArticleOutlinedIcon from "@mui/icons-material/ArticleOutlined";
import ArrowForwardIcon from "@mui/icons-material/ArrowForward";

export function Dashboard() {
  const navigate = useNavigate();
  const { data: agendas, isLoading, isError } = useAgendas();

  if (isLoading) {
    return (
      <Box
        sx={{
          display: "flex",
          justifyContent: "center",
          alignItems: "center",
          height: 256,
        }}
      >
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
      <Stack direction="row" sx={{ alignItems: "center", justifyContent: "space-between", mb: 4 }}>
        <Typography variant="h5" sx={{ fontWeight: "bold", color: "text.primary" }}>
          Pautas de Votação
        </Typography>
        <Button
          variant="contained"
          startIcon={<AddIcon />}
          sx={{ borderRadius: 2, textTransform: "none", fontWeight: "bold" }}
        >
          Nova Pauta
        </Button>
      </Stack>

      {agendas?.length === 0 ? (
        <Paper
          elevation={0}
          sx={{
            textAlign: "center",
            py: 8,
            borderRadius: 3,
            border: "1px solid",
            borderColor: "divider",
          }}
        >
          <ArticleOutlinedIcon sx={{ fontSize: 48, color: "text.disabled", mb: 2 }} />
          <Typography
            variant="subtitle1"
            sx={{
              color: "text.secondary",
              fontWeight: "medium",
            }}
          >
            Nenhuma pauta cadastrada ainda.
          </Typography>
          <Typography variant="body2" sx={{ color: "text.disabled", mt: 0.5 }}>
            Clique em "Nova Pauta" para começar.
          </Typography>
        </Paper>
      ) : (
        <Grid container spacing={3}>
          {agendas?.map((agenda) => (
            <Grid size={4} key={agenda.id}>
              <Card
                elevation={0}
                onClick={() => navigate(`/agenda/${agenda.id}`)}
                sx={{
                  height: "100%",
                  display: "flex",
                  flexDirection: "column",
                  borderRadius: 3,
                  border: "1px solid",
                  borderColor: "divider",
                  cursor: "pointer",
                  transition: "all 0.2s ease-in-out",
                  "&:hover": {
                    borderColor: "primary.main",
                    boxShadow: 2,
                  },
                }}
              >
                <CardContent sx={{ flexGrow: 1, p: 3, pb: 0 }}>
                  <Typography variant="h6" sx={{ fontWeight: "bold" }} gutterBottom>
                    {agenda.title}
                  </Typography>
                  <Typography
                    variant="body2"
                    color="text.secondary"
                    sx={{
                      display: "-webkit-box",
                      WebkitLineClamp: 3,
                      WebkitBoxOrient: "vertical",
                      overflow: "hidden",
                      lineHeight: 1.6,
                    }}
                  >
                    {agenda.description}
                  </Typography>
                </CardContent>

                <CardActions
                  sx={{
                    justifyContent: "space-between",
                    px: 3,
                    py: 2,
                    mt: 2,
                    borderTop: "1px solid",
                    borderColor: "grey.50",
                  }}
                >
                  <Box
                    sx={{
                      bgcolor: "grey.100",
                      color: "text.secondary",
                      px: 1,
                      py: 0.5,
                      borderRadius: 1,
                      fontSize: "0.75rem",
                      fontWeight: "bold",
                    }}
                  >
                    ID: #{agenda.id}
                  </Box>
                  <Stack direction="row" spacing={0.5} sx={{ alignItems: "center", color: "primary.main" }}>
                    <Typography variant="body2" sx={{ fontWeight: "medium" }}>
                      Acessar Sessão
                    </Typography>
                    <ArrowForwardIcon sx={{ fontSize: 16 }} />
                  </Stack>
                </CardActions>
              </Card>
            </Grid>
          ))}
        </Grid>
      )}
    </Box>
  );
}
