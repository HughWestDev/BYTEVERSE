import { useMemo, useState } from 'react';
import { Check, Copy, FileCode2, FolderGit2, Smartphone, Terminal } from 'lucide-react';

const androidModules = import.meta.glob('/android/**/*', {
  query: '?raw',
  import: 'default',
  eager: true,
}) as Record<string, string>;

export default function App() {
  const filePaths = useMemo(
    () =>
      Object.keys(androidModules).sort((a, b) => {
        const aIsKt = a.endsWith('.kt');
        const bIsKt = b.endsWith('.kt');
        if (aIsKt !== bIsKt) return aIsKt ? -1 : 1;
        return a.localeCompare(b);
      }),
    []
  );

  const [selectedPath, setSelectedPath] = useState<string>(
    () =>
      filePaths.find((p) => p.endsWith('MainActivity.kt')) ??
      filePaths[0] ??
      ''
  );
  const [copied, setCopied] = useState(false);

  const selectedContent = selectedPath ? androidModules[selectedPath] ?? '' : '';

  const handleCopy = async () => {
    if (!selectedContent) return;
    await navigator.clipboard.writeText(selectedContent);
    setCopied(true);
    window.setTimeout(() => setCopied(false), 1800);
  };

  return (
    <div className="min-h-screen bg-[#050508] text-slate-100 flex flex-col">
      <header className="border-b border-purple-500/25 bg-[#0B0B12] px-6 py-4 flex flex-wrap items-center justify-between gap-4">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 rounded-xl bg-gradient-to-br from-purple-600 to-violet-900 border border-purple-400/40 flex items-center justify-center shadow-[0_0_20px_rgba(168,85,247,0.35)]">
            <Smartphone className="w-5 h-5 text-white" />
          </div>
          <div>
            <div className="flex items-center gap-2">
              <h1 className="text-lg font-extrabold tracking-wider text-white">BYTEVERSE</h1>
              <span className="text-[11px] font-mono px-2 py-0.5 rounded bg-purple-500/15 border border-purple-400/40 text-purple-300">
                NATIVE ANDROID • KOTLIN + COMPOSE
              </span>
            </div>
            <p className="text-xs text-slate-400">
              Explore Your Developer Universe — by WEST.DEV
            </p>
          </div>
        </div>

        <div className="flex items-center gap-2 text-xs font-mono text-purple-300 bg-[#12121D] border border-purple-500/30 rounded-lg px-3 py-2">
          <FolderGit2 className="w-4 h-4 text-purple-400" />
          <span>Project Root: /android ({filePaths.length} files)</span>
        </div>
      </header>

      <div className="flex-1 grid grid-cols-1 lg:grid-cols-[320px_1fr] min-h-0">
        <aside className="border-r border-purple-500/20 bg-[#08080F] p-4 overflow-y-auto max-h-[40vh] lg:max-h-[calc(100vh-73px)]">
          <div className="text-[11px] font-mono uppercase tracking-widest text-slate-400 mb-3">
            Android Project Files
          </div>
          <div className="space-y-1">
            {filePaths.map((path) => {
              const active = path === selectedPath;
              const shortLabel = path.replace('/android/', '');
              return (
                <button
                  key={path}
                  type="button"
                  onClick={() => setSelectedPath(path)}
                  className={`w-full text-left px-3 py-2 rounded-lg text-xs font-mono flex items-center gap-2 transition-colors ${
                    active
                      ? 'bg-purple-600/25 text-purple-200 border border-purple-500/50'
                      : 'text-slate-400 hover:bg-white/5 hover:text-slate-200 border border-transparent'
                  }`}
                >
                  <FileCode2 className="w-3.5 h-3.5 shrink-0 text-purple-400" />
                  <span className="truncate">{shortLabel}</span>
                </button>
              );
            })}
          </div>
        </aside>

        <main className="flex flex-col min-w-0 bg-[#050508]">
          <div className="border-b border-purple-500/20 bg-[#0B0B12] px-5 py-3 flex items-center justify-between gap-4">
            <div className="flex items-center gap-2 min-w-0">
              <Terminal className="w-4 h-4 text-purple-400 shrink-0" />
              <span className="text-xs font-mono text-purple-200 truncate">
                {selectedPath || 'Select a file'}
              </span>
            </div>
            <button
              type="button"
              onClick={handleCopy}
              className="shrink-0 inline-flex items-center gap-1.5 px-3 py-1.5 rounded-lg bg-purple-600 hover:bg-purple-500 text-white text-xs font-mono transition-colors"
            >
              {copied ? <Check className="w-3.5 h-3.5" /> : <Copy className="w-3.5 h-3.5" />}
              <span>{copied ? 'Copied' : 'Copy File'}</span>
            </button>
          </div>

          <div className="flex-1 overflow-auto p-5 max-h-[60vh] lg:max-h-[calc(100vh-130px)]">
            <pre className="text-xs font-mono leading-relaxed text-slate-200 whitespace-pre">
              {selectedContent}
            </pre>
          </div>
        </main>
      </div>
    </div>
  );
}
