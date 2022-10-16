<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  
  <xsl:key name="reviewsByLocation" match="tweet" use="user"/>
  
  <xsl:template match="/">
    <html>
      <head>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-1BmE4kWBq78iYhFldvKuhfTAU6auU8tT94WrHftjDbrCEXSU1oBoqyl2QvZ6jIW3" crossorigin="anonymous"/>
      </head>
      <body>
        <div class="container">
          <nav class="navbar sticky-top navbar-light mb-2" style="background-color: #e3f2fd;">
            <div class="container-fluid">
              <span class="navbar-brand mb-0 h1">Twitter data</span>
            </div>
          </nav>
          
          <xsl:for-each select="data/users/user">
            <div class="card border-primary mb-2" style="max-width: 18rem;">
              <h5 class="card-header bg-transparent border-success">@<xsl:value-of select="username"/></h5>
              <div class="card-body">
                <p class="card-text p-2 bg-transparent">
                  <div class="row align-items-start">
                    <div class="col-4">
                      Age: <xsl:value-of select="age"/>
                    </div>
                    <div class="col-8">
                      Country: <xsl:value-of select="country"/>
                    </div>
                  </div>
                </p>
              </div>
              <div class="card-footer bg-transparent border-success">
                <button type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#reviewsAboutLocation{id}">
                  Show tweets
                </button>
              </div>
            </div>
          </xsl:for-each>
        </div>
        
        <xsl:for-each select="data/users/user">
          <div class="modal fade" id="reviewsAboutLocation{id}" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
            <div class="modal-dialog">
              <div class="modal-content">
                <div class="modal-header">
                  <h5 class="modal-title" id="exampleModalLabel">tweets from <xsl:value-of select="username"/></h5>
                  <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button></div>
                <div class="modal-body">
                  <ol class="list-group">
                    <xsl:for-each select="key('reviewsByLocation', id)">
                      <li class="list-group-item d-flex justify-content-between align-items-star">
                        <div class="ms-2 me-auto">
                          <div class="fw-light"><xsl:value-of select="text"/></div>
                          <div class="col-6">likes: <mark><xsl:value-of select="likes"/></mark></div>
                          <div class="col-6">retweets: <mark><xsl:value-of select="retweets"/></mark></div>
                        </div>
                        <small><xsl:value-of select="date"/></small>
                      </li>
                    </xsl:for-each>
                  </ol>
                </div>
                <div class="modal-footer"><button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button></div>
              </div>
            </div>
          </div>
          
        </xsl:for-each>
        
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-ka7Sk0Gln4gmtz2MlQnikT1wXgYsOg+OMhuP+IlRH9sENBO0LRn5q+8nbTov4+1p" crossorigin="anonymous"></script>
      </body>
    </html>
  </xsl:template>
</xsl:stylesheet>
