import React from 'react';
import './RightSidebar.css';

export const RightSidebar: React.FC = () => {
    return (
        <div className="right-sidebar">
            <div className="sidebar-header">
                <h1 className="sidebar-title">COMMUNITY</h1>
            </div>

            <div className="section">
                <h2 className="section-header">FRIENDS</h2>
                <div className="section-content">
                    {/* Future implementation: Friends list */}
                    <div className="friends-list">
                        {/* Friend items will be added here */}
                    </div>
                </div>
            </div>

            <div className="section">
                <h2 className="section-header">CHAT</h2>
                <div className="section-content">
                    {/* Future implementation: Chat list */}
                    <div className="chat-list">
                        {/* Chat items will be added here */}
                    </div>
                </div>
            </div>
        </div>
    );
};