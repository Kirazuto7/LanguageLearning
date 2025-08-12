import { useState, useEffect } from "react";

interface UseFetchResult<T>{
    data: T | null;
    isLoading: boolean;
    error: string | null;
}

export default function useFetch<T = unknown>(url: string, options?: RequestInit): UseFetchResult<T> {
    const [data, setData] = useState<T | null>(null);
    const [isLoading, setIsLoading] = useState<boolean>(true);
    const [error, setError] = useState<string | null>(null);
    
    useEffect(() => {
        const abortController = new AbortController();

        const fetchData = async () => {
            setIsLoading(true);
            setError(null);

            try {
                const response = await fetch(url, {
                    ...options,
                    signal: abortController.signal,
                });

                if(!response.ok) {
                    throw new Error('Failed to fetch data. The server responded with an error.');
                }

                const json = await response.json() as T;
                setData(json);
            } catch (err) {
                if(err instanceof Error){
                    if(err.name !== 'AbortError') {
                        setError(err.message);
                    }
                }
            } finally {
                if(!abortController.signal.aborted) {
                    setIsLoading(false);
                }
            }
        };
        fetchData();

        return () => {
            abortController.abort();
        }
    }, [url, JSON.stringify(options)]);
    
    return { data, isLoading, error };
}