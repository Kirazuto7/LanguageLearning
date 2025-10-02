import React, { Component, ErrorInfo, ReactNode } from 'react';

interface Props {
    children: ReactNode;
}

interface State {
    hasError: boolean;
}

class ChunkLoadErrorBoundary extends Component<Props, State> {
    constructor(props: Props) {
        super(props);
        this.state = { hasError: false };
    }

    static getDerivedStateFromError(error: Error): State {
        // Check if the error is a chunk load error
        if (error.name === 'ChunkLoadError') {
            // Force a hard reload of the page. This often fixes the issue by
            // clearing stale cache and fetching the latest chunks.
            window.location.reload();
            return { hasError: true };
        }
        // For other errors, just update state to render the fallback UI
        return { hasError: true };
    }

    componentDidCatch(error: Error, errorInfo: ErrorInfo) {
        console.error("Uncaught error:", error, errorInfo);
    }

    render() {
        return this.props.children;
    }
}

export default ChunkLoadErrorBoundary;