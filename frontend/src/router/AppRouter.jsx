import { BrowserRouter, Routes, Route, Navigate} from "react-router-dom";

import CustomerStartPage from "../pages/CustomerStarterPage";
import TicketPage from "../pages/TicketPage";

function AppRouter(){
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/start" element={<CustomerStartPage />} />
                <Route path="/ticket/:ticketId" element={<TicketPage />} />

                <Route
                    path="*"
                    element={<Navigate to="/start" replace />}
                />
            </Routes>
        </BrowserRouter>
    )
}

export default AppRouter;