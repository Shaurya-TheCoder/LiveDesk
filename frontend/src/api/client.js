const API_BASE_URL = "";

export async function apiFetch(path, options = {}) {
    const response = await fetch(`${API_BASE_URL}${path}`, {
        ...options,
        headers: {
            "Content-Type": "application/json",
            ...options.headers
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