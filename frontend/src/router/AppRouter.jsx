import { BrowserRouter, Routes, Route, Navigate} from "react-router-dom";

import CustomerStarterPage from "../pages/CustomerStarterPage";
import TicketPage from "../pages/TicketPage";
import AgentLogin from "../pages/AgentLogin";
import Header from "../components/Header";
import Footer from "../components/Footer";
import AgentDashboardPage from "../pages/AgentDashboard";
import ProtectedRoute from "./ProtectedRoute";
import RateLimitPage from "../pages/RateLimitPage";
import AdminDashboardPage from "../pages/AdminDashboard";
import AgentRegister from "../pages/AgentRegister";

function AppRouter(){
    return (
        <BrowserRouter>
        <Header />
        <main>
            <Routes>
                <Route path="/home" element={<CustomerStarterPage />} />
                <Route path="/agent/login" element={<AgentLogin />} />

                <Route path="/ticket/:ticketId" element={<TicketPage />} />
                
                <Route element={<ProtectedRoute />}>
                    <Route path="/agent/dashboard" element={<AgentDashboardPage />} />
                    <Route path="/admin/register-agent" element={<AgentRegister />} />
                    <Route path="/admin/dashboard" element={<AdminDashboardPage />} />
                </Route>

                <Route path="/too-many-requests" element={<RateLimitPage />} />
                <Route
                    path="*"
                    element={<Navigate to="/home" replace />}
                />
            </Routes>
        </main>
        <Footer />
        </BrowserRouter>
    )
}

export default AppRouter;