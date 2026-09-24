import useAuthStore from "../stores/authStore";
const API_BASE_URL = "";

export async function apiFetch(path, options = {}) {
    const {skipAuth = false, ...fetchOptions} = options;
    const token = useAuthStore.getState().token;

    const response = await fetch(`${API_BASE_URL}${path}`, {
        ...fetchOptions,
        headers: {
            "Content-Type": "application/json",
            ...(!skipAuth && token && {
                Authorization: `Bearer ${token}`
            }),
            ...fetchOptions.headers
        }
    });

    if (!response.ok) {
        let error;

        try {
            error = await response.json();
        } catch {
            error = {
                message: "Something went wrong"
            };
        }

        throw {
            status: response.status,
            ...error
        };
    }

    if (response.status === 204) {
        return null;
    }

    return response.json();
}