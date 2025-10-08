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
        // Update state so the next render will show the fallback UI or nothing.
        return { hasError: true };
    }

    componentDidCatch(error: Error, errorInfo: ErrorInfo) {
        console.error("Uncaught error:", error, errorInfo);

        // Check if the error is a chunk load error and if we haven't already tried to reload.
        if (error.name === 'ChunkLoadError' && !this.state.hasError) {
            // Force a hard reload of the page. This often fixes the issue by
            // clearing stale cache and fetching the latest chunks.
            window.location.reload();
        }
    }

    render() {
        if (this.state.hasError) {
            // You can render a fallback UI here, like a loading spinner or a message.
            // Returning null will prevent the broken component tree from rendering.
            return null;
        }
        return this.props.children;
    }
}

export default ChunkLoadErrorBoundary;