import { Navigate, Outlet } from 'react-router-dom';
import useAuthStore from '../stores/authStore';

function protectedRoute(){
    const isAuthenticated = useAuthStore((state) => state.isAuthenticated);

    if(!isAuthenticated){
        return <Navigate to="agent/login" replace />
    }

    return <Outlet />;
}

export default protectedRoute;