import React from 'react';

// The first argument to forwardRef should be the render function (props, ref) => ...
// The props argument is unused here, so we can name it `_props`.
const BehindCoverPage = React.forwardRef((_props, ref) => {
    return (
        <div ref={ref}>
            <div className="d-flex justify-content-center align-items-center" style={{backgroundColor: '#333', width: '100%', height: '100%', boxShadow: '0 0 20px #ff69b4'}}>
                <div className="ms-3" style={{backgroundColor: 'white', width: '98%', height: '95%'}}></div>
            </div>
        </div>
    )
});

export default BehindCoverPage;