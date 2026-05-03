function SettingsPage() {
  return (
    <>
      <header className="dashboard-header">
        <h1>Settings</h1>
        <p>Theme and search behavior preferences.</p>
      </header>

      <section className="dash-card settings-list">
        <div className="setting-row"><span>Theme</span><strong>Main Green/Gold</strong></div>
        <div className="setting-row"><span>Autocomplete</span><strong>Enabled</strong></div>
        <div className="setting-row"><span>Search Limit</span><strong>Up to 20 results</strong></div>
      </section>
    </>
  );
}

export default SettingsPage;
