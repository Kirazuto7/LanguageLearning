import React, { useState, useEffect, RefObject } from "react";

/**
 * A hook that detects if an element is currently visible in the viewport.
 * @param ref A React ref attached to the element to observe
 */
function useOnScreen(ref: React.RefObject<HTMLElement>): boolean {
    const [isIntersecting, setIntersecting] = useState(false);

    useEffect(() => {
        const observer = new IntersectionObserver(
            ([entry]) => {
                setIntersecting(entry.isIntersecting);
            }, {
                threshold: 0.1 // Triggered when at least 10% of the element is visible
            }
        );

        const currentRef = ref.current;
        if (currentRef) {
            observer.observe(currentRef);
        }

        return () => {
            if (currentRef) {
                observer.unobserve(currentRef);
            }
        }
    }, [ref]);

    return isIntersecting;
}

export default useOnScreen;