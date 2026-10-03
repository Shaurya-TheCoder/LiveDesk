import React from 'react';

export default function Footer() {
  return (
    <footer className="w-full bg-white border-t border-gray-200 py-4 px-6 text-center text-sm text-gray-600">
      <p>© {new Date().getFullYear()} Livedesk. All rights reserved.</p>
    </footer>
  );
}