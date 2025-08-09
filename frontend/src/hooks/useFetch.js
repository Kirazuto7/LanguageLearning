import { useState, useEffect } from "react";

export default function useFetch(url, options) {
    const [data, setData] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    
    useEffect(() => {
        let isMounted = true;

        async function fetchData() {
            setLoading(true);
            setError(null);

            try {
                const response = await fetch(url, options);

                if(!response.ok) {
                    throw new Error('Failed to fetch data. The server responded with an error.');
                }

                const json = await response.json();
                if(isMounted) setData(json);
            } catch (err) {
                if(isMounted) setError(err.message);
            } finally {
                if(isMounted) setLoading(false);
            }
        }
        fetchData();

        return () => {
            isMounted = false;
        }
    }, [url, options]);
    
    return { data, loading, error };
}