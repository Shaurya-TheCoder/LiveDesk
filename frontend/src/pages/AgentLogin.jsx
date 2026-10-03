import { Navigate } from "react-router-dom";
import LoginForm from "../components/LoginForm";
import useAuthStore from "../stores/authStore";

function AgentLogin(){
    const isAuthenticated = useAuthStore((state) => state.isAuthenticated);
    const role = useAuthStore((state) => state.role);

    if(isAuthenticated){
        if(role === "ADMIN")
            return <Navigate to="/admin/dashboard" replace={true}/>
        return <Navigate to="/agent/dashboard" replace={true}/>
    }
    return(
        <LoginForm />
    )
}

export default AgentLogin;