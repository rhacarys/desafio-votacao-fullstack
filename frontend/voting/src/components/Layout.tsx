import { Outlet } from "react-router-dom";
import { Box, Container, Typography, AppBar, Toolbar } from "@mui/material";
import HowToVoteIcon from "@mui/icons-material/HowToVote";

export function Layout() {
  return (
    <Box sx={{ minHeight: "100vh", bgcolor: "grey.50" }}>
      <AppBar position="static" color="inherit" elevation={0} sx={{ borderBottom: 1, borderColor: "divider" }}>
        <Container maxWidth="lg">
          <Toolbar disableGutters sx={{ height: 64 }}>
            <HowToVoteIcon sx={{ color: "primary.main", fontSize: 32, mr: 1.5 }} />
            <Typography
              variant="h6"
              component="h1"
              sx={{
                fontWeight: "bold",
                color: "text.primary",
                letterSpacing: "-0.025em",
              }}
            >
              Desafio de Votação
            </Typography>
          </Toolbar>
        </Container>
      </AppBar>

      <Container component="main" maxWidth="lg" sx={{ py: 4 }}>
        <Outlet />
      </Container>
    </Box>
  );
}
