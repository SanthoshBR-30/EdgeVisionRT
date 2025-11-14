"use strict";
// EdgeVisionRT Web Viewer
// Displays processed frames from Android app
class FrameViewer {
    constructor() {
        this.frameElement = document.getElementById('frame-display');
        this.resolutionElement = document.getElementById('resolution');
        this.fpsElement = document.getElementById('fps');
        this.statusElement = document.getElementById('status');
        this.initialize();
    }
    initialize() {
        console.log('EdgeVisionRT Web Viewer initialized');
        this.updateStatus('Waiting for frame data...');
        // Load sample processed frame (placeholder for now)
        this.loadSampleFrame();
    }
    loadSampleFrame() {
        // Placeholder: Gray rectangle representing processed frame
        // In real implementation, this will receive base64 image from Android
        const canvas = document.createElement('canvas');
        canvas.width = 640;
        canvas.height = 480;
        const ctx = canvas.getContext('2d');
        if (ctx) {
            // Draw placeholder
            ctx.fillStyle = '#333';
            ctx.fillRect(0, 0, 640, 480);
            ctx.fillStyle = '#fff';
            ctx.font = '24px Arial';
            ctx.textAlign = 'center';
            ctx.fillText('Processed Frame Placeholder', 320, 240);
            this.frameElement.src = canvas.toDataURL();
            this.updateStats({ resolution: '640x480', fps: 15, processing: 'Edge Detection' });
            this.updateStatus('Sample frame loaded');
        }
    }
    updateFrame(base64Image) {
        this.frameElement.src = base64Image;
        this.updateStatus('Frame updated');
    }
    updateStats(stats) {
        this.resolutionElement.textContent = stats.resolution;
        this.fpsElement.textContent = stats.fps.toString();
    }
    updateStatus(status) {
        this.statusElement.textContent = status;
        console.log(`Status: ${status}`);
    }
}
// Initialize viewer when DOM is ready
document.addEventListener('DOMContentLoaded', () => {
    const viewer = new FrameViewer();
    console.log('Web viewer ready');
});
