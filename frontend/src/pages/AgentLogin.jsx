import { Navigate } from "react-router-dom";
import LoginForm from "../components/LoginForm";
import useAuthStore from "../stores/authStore";

function AgentLogin(){
    const isAuthenticated = useAuthStore((state) => state.isAuthenticated);

    if(isAuthenticated){
        return <Navigate to="/agent/dashboard" replace={true}/>
    }
    return(
        <LoginForm />
    )
}

export default AgentLogin;