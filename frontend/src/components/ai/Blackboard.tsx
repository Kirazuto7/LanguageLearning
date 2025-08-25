import React, {useEffect, useState} from "react";
import styles from "./mascot.module.scss";
import {useSettingsManager} from "../../hooks/useSettingsManager";
import Settings from "../learningtoolsnav/Settings";

interface BlackboardProps {
    text: string;
}

const Blackboard: React.FC<BlackboardProps> = ({text}) => {
    return(
        <div className="relative" style={{ filter: 'drop-shadow(0 10px 20px rgba(0,0,0,0.3))' }}>
            <svg width="280" height="200" viewBox="0 0 220 160">
                <defs>
                    {/* Wood frame gradient */}
                    <linearGradient id="woodGradient" x1="0%" y1="0%" x2="100%" y2="100%">
                        <stop offset="0%" stopColor="#8B4513" stopOpacity="1" />
                        <stop offset="50%" stopColor="#A0522D" stopOpacity="1" />
                        <stop offset="100%" stopColor="#654321" stopOpacity="1" />
                    </linearGradient>

                    {/* Blackboard texture */}
                    <pattern id="chalkboardTexture" patternUnits="userSpaceOnUse" width="2" height="2">
                        <rect width="2" height="2" fill="#1a1a1a"/>
                        <circle cx="1" cy="1" r="0.3" fill="#2a2a2a" opacity="0.3"/>
                    </pattern>

                    {/* Chalk dust effect */}
                    <filter id="chalkDust">
                        <feTurbulence baseFrequency="0.9" numOctaves="4" result="noise"/>
                        <feDisplacementMap in="SourceGraphic" in2="noise" scale="1"/>
                    </filter>

                    {/* Glow effect for chalk text */}
                    <filter id="chalkGlow">
                        <feGaussianBlur stdDeviation="0.5" result="coloredBlur"/>
                        <feMerge>
                            <feMergeNode in="coloredBlur"/>
                            <feMergeNode in="SourceGraphic"/>
                        </feMerge>
                    </filter>
                </defs>

                {/* Wooden frame */}
                <rect x="0" y="0" width="220" height="160" fill="url(#woodGradient)" rx="8"/>
                <rect x="4" y="4" width="212" height="152" fill="url(#woodGradient)" rx="6"/>

                {/* Main blackboard surface */}
                <rect x="12" y="12" width="196" height="120" fill="url(#chalkboardTexture)" rx="3"/>

                {/* Inner shadow for depth */}
                <rect x="12" y="12" width="196" height="120" fill="none" stroke="rgba(0,0,0,0.3)" strokeWidth="1" rx="3"/>

                {/* Chalk tray */}
                <rect x="16" y="130" width="188" height="12" fill="url(#woodGradient)" rx="2"/>
                <rect x="18" y="131" width="184" height="4" fill="#654321" rx="1"/>

                {/* Chalk pieces in tray */}
                <rect x="25" y="133" width="8" height="3" fill="#f8f8f8" rx="1.5"/>
                <rect x="38" y="133" width="6" height="2.5" fill="#ffeb3b" rx="1.25"/>
                <rect x="48" y="133" width="7" height="3" fill="#4caf50" rx="1.5"/>

                {/* Eraser */}
                <rect x="170" y="132" width="12" height="6" fill="#8B4513" rx="1"/>
                <rect x="171" y="133" width="10" height="2" fill="#D2691E" rx="0.5"/>

                {/* Main text element using foreignObject for wrapping */}
                <foreignObject x="20" y="16" width="180" height="112" filter="url(#chalkGlow)">
                    <div
                        style={{
                            color: 'white',
                            fontFamily: "'Courier New', monospace",
                            fontSize: '14px',
                            textAlign: 'center',
                            display: 'flex',
                            alignItems: 'center',
                            justifyContent: 'center',
                            height: '100%',
                            lineHeight: '1.4',
                            wordWrap: 'break-word',
                            textShadow: '0 0 5px rgba(255,255,255,0.5)'
                        }}
                    >
                        {text}
                    </div>
                </foreignObject>

                {/* Subtle chalk dust overlay */}
                <rect
                    x="12"
                    y="12"
                    width="196"
                    height="120"
                    fill="white"
                    opacity="0.02"
                    filter="url(#chalkDust)"
                />
            </svg>
        </div>
    )
}

export default Blackboard;